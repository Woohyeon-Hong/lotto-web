package io.woohyeon.lotto.lotto_web.repository;

import static io.woohyeon.lotto.lotto_web.support.LottoRules.LOTTO_PRICE;

import io.woohyeon.lotto.lotto_web.model.Lotto;
import io.woohyeon.lotto.lotto_web.model.PurchaseLog;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class PurchaseStore {

    private Long sequence = 1L;
    private final Map<Long, PurchaseLog> purchaseLogs = new LinkedHashMap<Long, PurchaseLog>();

    public Long save(List<Lotto> lottos) {
        PurchaseLog log = new PurchaseLog(sequence, lottos.size() * LOTTO_PRICE, lottos, LocalDateTime.now());
        purchaseLogs.put(sequence, log);
        return sequence++;
    }

    public Optional<PurchaseLog> findById(long id) {
        return Optional.ofNullable(purchaseLogs.get(id));
    }

    public List<PurchaseLog> findAll() {
        return new ArrayList<>(purchaseLogs.values());
    }
}
