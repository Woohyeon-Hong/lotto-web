package io.woohyeon.lotto.lotto_web.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.woohyeon.lotto.lotto_web.dto.request.LottoPurchaseRequest;
import io.woohyeon.lotto.lotto_web.dto.response.LottoPurchaseResponse;
import io.woohyeon.lotto.lotto_web.repository.ResultStore;
import io.woohyeon.lotto.lotto_web.support.LottoRules;
import io.woohyeon.lotto.lotto_web.repository.PurchaseStore;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LottoServiceTest {

    PurchaseStore purchaseStore;
    ResultStore resultStore;
    LottoService lottoService;

    @BeforeEach
    void beforeEach() {
        purchaseStore = new PurchaseStore();
        lottoService = new LottoService(purchaseStore, resultStore);
    }

    @Test
    void purchaseLottosWith_주어진_금액으로_로또를_구매한다() {
        //given
        List<LottoPurchaseRequest> corrects = List.of(
                new LottoPurchaseRequest(1000),
                new LottoPurchaseRequest(2000),
                new LottoPurchaseRequest(10000),
                new LottoPurchaseRequest(11000)
        );

        List<LottoPurchaseRequest> wrongs = List.of(
                new LottoPurchaseRequest(1001),     //1000의 배수 x
                new LottoPurchaseRequest(0),        //0
                new LottoPurchaseRequest(-1000)    //음수
        );

        //when
        List<LottoPurchaseResponse> purchaseResponses = corrects.stream()
                .map(lottoService::purchaseLottosWith)
                .toList();

        //then

        //투입한 금액을 로또 가격으로 나눈 개수의 로또를 발행한다.
        for (int i = 0; i < purchaseResponses.size(); i++) {
            assertThat(purchaseResponses.get(i).lottoCount())
                    .isEqualTo(corrects.get(i).purchaseAmount() / LottoRules.LOTTO_PRICE);
        }

        //1000의 배수인 자연수가 입력되지 않으면 예외가 발생한다.
        wrongs.forEach(wrong ->
                assertThatThrownBy(
                                () -> lottoService.purchaseLottosWith(wrong)
                        ).isInstanceOf(IllegalArgumentException.class)
        );
    }

//    @Test
//    void calculateStatisticsOf_로또의_당첨_내역을_계산한다() {
//        // given
//        List<Integer> lottoNumbers = List.of(1, 2, 3, 4, 5, 6);
//        int bonusNumber = 7;
//
//        List<IssuedLotto> issuedLottos = List.of(
//                new IssuedLotto(List.of(1, 2, 3, 4, 5, 6), null), // 1등
//                new IssuedLotto(List.of(1, 2, 3, 4, 5, 7), null), // 2등
//                new IssuedLotto(List.of(1, 2, 3, 4, 5, 8), null), // 3등
//                new IssuedLotto(List.of(1, 2, 3, 4, 8, 9), null)  // 4등
//        );
//
//        LottoResultRequest request = new LottoResultRequest(
//                issuedLottos,
//                lottoNumbers,
//                bonusNumber
//        );
//
//        // when
//        LottoResultResponse result = lottoService.calculateStatisticsOf(request);
//
//        // then
//        // 총 로또 개수는 요청한 issuedLottos의 개수와 같아야 한다.
//        assertThat(result.rankCounts().stream()
//                .mapToLong(entry -> entry.getValue())
//                .sum()).isEqualTo(issuedLottos.size());
//
//        // 수익률은 0 이상이어야 한다.
//        assertThat(result.returnRate()).isGreaterThanOrEqualTo(0);
//
//        // 로또 개수 × LOTTO_PRICE 만큼의 금액을 기준으로 계산된다.
//        int purchaseAmount = issuedLottos.size() * LottoRules.LOTTO_PRICE;
//        assertThat(purchaseAmount).isEqualTo(4000);
//
//        // 1등 로또(1~6 맞춤)가 1장 존재해야 함
//        boolean hasFirstPrize = result.rankCounts().stream()
//                .anyMatch(entry -> entry.getKey().name().equals("FIRST") && entry.getValue() == 1);
//        assertThat(hasFirstPrize).isTrue();
//    }
//
//    @Test
//    void getLottoExpectedStatistics_기록이_없을_때는_기본값을_반환한다() {
//        // given && when
//       ExpectedStatistics stats = lottoService.getLottoExpectedStatistics();
//
//        // then
//        assertThat(stats.totalSamples()).isZero();
//        assertThat(stats.averageReturnRate()).isZero();
//        assertThat(stats.accumulatedRankCounts()).isEmpty();
//    }
//
//    @Test
//    void getLottoExpectedStatistics_여러_구매기록을_집계하여_평균수익률과_등수별통계를_반환한다() {
//        // given
//        List<IssuedLotto> issuedLottos = List.of(
//                new IssuedLotto(List.of(1, 2, 3, 4, 5, 6), LocalDateTime.now())
//        );
//        PurchaseResponse purchase = new PurchaseResponse(1, issuedLottos);
//
//        // 첫 번째 기록: 수익률 100% -  5등 1회
//        LottoResultResponse result1 =
//                new LottoResultResponse(List.of(Map.entry(Rank.FIFTH, 1L)), 100.0);
//        purchaseStore.save(purchase, result1);
//
//        // 두 번째 기록: 수익률 50% - 4등 1회, 5등 2회
//        LottoResultResponse result2 =
//                new LottoResultResponse(List.of(Map.entry(Rank.FOURTH, 1L), Map.entry(Rank.FIFTH, 2L)), 50.0);
//        purchaseStore.save(purchase, result2);
//
//        // when
//        ExpectedStatistics stats = lottoService.getLottoExpectedStatistics();
//
//        // then
//        assertThat(stats.totalSamples()).isEqualTo(2);
//        assertThat(stats.averageReturnRate()).isEqualTo(75.0); // (100 + 50) / 2
//
//        // 등수별 누적 카운트 검증
//        assertThat(stats.accumulatedRankCounts())
//                .extracting(Map.Entry::getKey)
//                .containsExactlyInAnyOrder(Rank.FOURTH, Rank.FIFTH);
//
//        assertThat(stats.accumulatedRankCounts())
//                .filteredOn(e -> e.getKey() == Rank.FOURTH)
//                .first()
//                .extracting(Map.Entry::getValue)
//                .isEqualTo(1L);
//
//        assertThat(stats.accumulatedRankCounts())
//                .filteredOn(e -> e.getKey() == Rank.FIFTH)
//                .first()
//                .extracting(Map.Entry::getValue)
//                .isEqualTo(3L); // 1 + 2
//    }
}