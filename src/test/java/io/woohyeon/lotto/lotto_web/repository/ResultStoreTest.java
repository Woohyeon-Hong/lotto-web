package io.woohyeon.lotto.lotto_web.repository;

import static io.woohyeon.lotto.lotto_web.support.LottoRules.LOTTO_PRICE;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import io.woohyeon.lotto.lotto_web.model.Lotto;
import io.woohyeon.lotto.lotto_web.model.PurchaseLog;
import io.woohyeon.lotto.lotto_web.model.RankCount;
import io.woohyeon.lotto.lotto_web.model.ResultRecord;
import io.woohyeon.lotto.lotto_web.model.WinningNumbers;
import io.woohyeon.lotto.lotto_web.support.LottoRules;
import io.woohyeon.lotto.lotto_web.support.LottoStatistics;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResultStoreTest {

    ResultStore resultStore;
    PurchaseStore purchaseStore;

    @BeforeEach
    void beforeEach() {
        resultStore = new ResultStore();
        purchaseStore = new PurchaseStore();
    }

    @Test
    void save_purchaseId를_id로_해서_저장한다() {
        //given
        List<Lotto> purchasedLottos = List.of(
                new Lotto(List.of(1, 2, 3, 4, 5, 6)),
                new Lotto(List.of(7, 8, 9, 10, 11, 12)),
                new Lotto(List.of(1, 3, 5, 7, 9, 11))
        );
        Long purchaseId = purchaseStore.save(purchasedLottos);

        WinningNumbers winningNumbers = new WinningNumbers(List.of(1, 2, 3, 4, 5, 6), 12);

        LottoStatistics lottoStatistics = new LottoStatistics(winningNumbers, purchasedLottos,
                purchasedLottos.size() * LOTTO_PRICE);
        lottoStatistics.compute();

        List<RankCount> rankCounts = RankCount.fromEntries(lottoStatistics.getRankCounts());

        //when
        Long savedId = resultStore.save(purchaseId, winningNumbers, lottoStatistics.getRateOfReturn(), rankCounts);

        //then
        ResultRecord resultRecord = resultStore.findByPurchaseId(savedId).get();

        assertThat(resultRecord.getReturnRate()).isEqualTo(lottoStatistics.getRateOfReturn());
        assertThat(resultRecord.getWinningNumbers()).isEqualTo(winningNumbers);
        assertThat(resultRecord.getRankCounts()).isEqualTo(rankCounts);
    }

    @Test
    void findAll_모든_당첨_내역을_반환한다() {
        //given

        //구매 및 당첨 내역 계산 1
        List<Lotto> purchasedLottos1 = List.of(new Lotto(List.of(1, 2, 3, 4, 5, 6)));
        Long purchaseId1 = purchaseStore.save(purchasedLottos1);

        WinningNumbers winningNumbers1 = new WinningNumbers(List.of(1, 2, 3, 4, 5, 6), 12);

        LottoStatistics lottoStatistics = new LottoStatistics(winningNumbers1, purchasedLottos1,
                purchasedLottos1.size() * LOTTO_PRICE);
        lottoStatistics.compute();

        Long savedId1 = resultStore.save(purchaseId1, winningNumbers1, lottoStatistics.getRateOfReturn(), RankCount.fromEntries(lottoStatistics.getRankCounts()));

        //구매 및 당첨 내역 계산 2
        List<Lotto> purchasedLottos2 = List.of(new Lotto(List.of(1, 3, 5, 7, 9, 11)));
        Long purchaseId2 = purchaseStore.save(purchasedLottos2);

        WinningNumbers winningNumbers2 = new WinningNumbers(List.of(1, 2, 3, 4, 5, 6), 12);

        lottoStatistics = new LottoStatistics(winningNumbers2, purchasedLottos2,
                purchasedLottos2.size() * LOTTO_PRICE);
        lottoStatistics.compute();

        Long savedId2 = resultStore.save(purchaseId2, winningNumbers2, lottoStatistics.getRateOfReturn(), RankCount.fromEntries(lottoStatistics.getRankCounts()));

        //when
        List<ResultRecord> result = resultStore.findAll();

        //then
        assertThat(result.size()).isEqualTo(2);

        assertThat(savedId1).isEqualTo(purchaseId1);
        assertThat(savedId2).isEqualTo(purchaseId2);

        assertThat(result.get(0).getCreatedAt()).isBefore(result.get(1).getCreatedAt());

        assertThat(result.get(0).getWinningNumbers()).isEqualTo(winningNumbers1);
        assertThat(result.get(1).getWinningNumbers()).isEqualTo(winningNumbers2);
    }
}