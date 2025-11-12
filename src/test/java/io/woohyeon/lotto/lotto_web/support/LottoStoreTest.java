package io.woohyeon.lotto.lotto_web.support;

import static org.assertj.core.api.Assertions.assertThat;

import io.woohyeon.lotto.lotto_web.dto.response.IssuedLotto;
import io.woohyeon.lotto.lotto_web.dto.response.LottoResultResponse;
import io.woohyeon.lotto.lotto_web.dto.response.PurchaseResponse;
import io.woohyeon.lotto.lotto_web.model.Lotto;
import io.woohyeon.lotto.lotto_web.model.PurchaseLog;
import io.woohyeon.lotto.lotto_web.model.Rank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LottoStoreTest {
    LottoStore lottoStore;

    @BeforeEach
    void beforeEach() {
        lottoStore = new LottoStore();
    }

    @Test
    void save_호출시_서로_다른_구매내역이_각각_저장된다() {
        // given
        List<IssuedLotto> lottos1 = List.of(new IssuedLotto(List.of(1, 2, 3, 4, 5, 6), LocalDateTime.now()));
        List<IssuedLotto> lottos2 = List.of(new IssuedLotto(List.of(7, 8, 9, 10, 11, 12), LocalDateTime.now()));

        PurchaseResponse purchase1 = new PurchaseResponse(1, lottos1);
        PurchaseResponse purchase2 = new PurchaseResponse(1, lottos2);

        LottoResultResponse result = new LottoResultResponse(List.of(Map.entry(Rank.FIFTH, 1L)), 80.5);

        // when
        long id1 = lottoStore.save(purchase1, result);
        long id2 = lottoStore.save(purchase2, result);

        // then
        assertThat(id1).isEqualTo(1L);
        assertThat(id2).isEqualTo(2L);
        assertThat(lottoStore.findById(id1)).isPresent();
        assertThat(lottoStore.findById(id2)).isPresent();
    }

    @Test
    void findRecentRecords_가장_최근_내역이_먼저_정렬되어_반환된다() {
        // given
        LocalDateTime earlier = LocalDateTime.now();
        LocalDateTime later = earlier.plusMinutes(10);

        PurchaseResponse purchase1 = new PurchaseResponse(1, List.of(new IssuedLotto(List.of(1, 2, 3, 4, 5, 6), earlier)));
        PurchaseResponse purchase2 = new PurchaseResponse(1, List.of(new IssuedLotto(List.of(7, 8, 9, 10, 11, 12), later)));

        LottoResultResponse result = new LottoResultResponse(List.of(Map.entry(Rank.FIFTH, 1L)), 90.0);

        // when
        long id1 = lottoStore.save(purchase1, result);
        long id2 = lottoStore.save(purchase2, result);

        List<PurchaseLog> records = lottoStore.findRecentRecords();

        // then
        assertThat(records).hasSize(2);

        // 가장 최근(later)이 먼저 나와야 한다
        assertThat(records.get(0).id()).isEqualTo(id2);
        assertThat(records.get(1).id()).isEqualTo(id1);
        assertThat(records.get(0).purchasedAt()).isAfter(records.get(1).purchasedAt());
    }


}