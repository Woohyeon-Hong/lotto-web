package io.woohyeon.lotto.lotto_web.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.woohyeon.lotto.lotto_web.model.Lotto;
import io.woohyeon.lotto.lotto_web.model.PurchaseLog;
import io.woohyeon.lotto.lotto_web.support.LottoRules;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseStoreTest {

    PurchaseStore purchaseStore;

    @BeforeEach
    void beforeEach() {
        purchaseStore = new PurchaseStore();
    }

    @Test
    void save_구매_내역이_저장된다() {
        //given
        List<Lotto> purchasedLottos = List.of(
                new Lotto(List.of(1, 2, 3, 4, 5, 6)),
                new Lotto(List.of(7, 8, 9, 10, 11, 12)),
                new Lotto(List.of(1, 3, 5, 7, 9, 11))
        );

        //when
        Long savedId = purchaseStore.save(purchasedLottos);

        //then
        PurchaseLog foundLog = purchaseStore.findById(savedId).get();
        purchasedLottos.stream()
                        .forEach(lotto ->
                                assertThat(foundLog.getIssuedLottos()).contains(lotto));
    }

    @Test
    void findAll_스토어에_저장된_모든_기록을_반환한다() {
        //given
        List<Lotto> purchase1 = List.of(
                new Lotto(List.of(1, 2, 3, 4, 5, 6)),
                new Lotto(List.of(7, 8, 9, 10, 11, 12)),
                new Lotto(List.of(1, 3, 5, 7, 9, 11))
        );
        purchaseStore.save(purchase1);

        List<Lotto> purchase2 = List.of(
                new Lotto(List.of(1, 2, 3, 4, 5, 6)),
                new Lotto(List.of(7, 8, 9, 10, 11, 12)),
                new Lotto(List.of(1, 3, 5, 7, 9, 11)),
                new Lotto(List.of(32, 31, 15, 17, 19, 11))
        );
        purchaseStore.save(purchase2);

        List<Lotto> purchase3 = List.of(
                new Lotto(List.of(1, 2, 3, 4, 5, 6)),
                new Lotto(List.of(7, 8, 9, 10, 11, 12))
        );
        purchaseStore.save(purchase3);

        //then
        List<PurchaseLog> result = purchaseStore.findAll();

        //then
        assertThat(result.size()).isEqualTo(3);

        //purchase1이 purchase2보다 먼저 일어난다.
        assertThat(result.get(0).getPurchasedAt()).isBefore(result.get(1).getPurchasedAt());
        //purchase2가 purchase3보다 먼저 일어난다.
        assertThat(result.get(1).getPurchasedAt()).isBefore(result.get(2).getPurchasedAt());

        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(2).getId()).isEqualTo(3L);

        assertThat(result.get(0).getPurchaseAmount()).isEqualTo(3 * LottoRules.LOTTO_PRICE);
        assertThat(result.get(1).getPurchaseAmount()).isEqualTo(4 * LottoRules.LOTTO_PRICE);
        assertThat(result.get(2).getPurchaseAmount()).isEqualTo(2 * LottoRules.LOTTO_PRICE);
    }
}