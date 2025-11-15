package io.woohyeon.lotto.lotto_web.model;


import java.time.LocalDateTime;
import java.util.List;

public class PurchaseLog {

    long id;
    int purchaseAmount;
    List<Lotto> issuedLottos;
    LocalDateTime purchasedAt;

    public PurchaseLog(long id, int purchaseAmount, List<Lotto> issuedLottos, LocalDateTime purchasedAt) {
        this.id = id;
        this.purchaseAmount = purchaseAmount;
        this.issuedLottos = issuedLottos;
        this.purchasedAt = purchasedAt;
    }

    public long getId() {
        return id;
    }

    public int getPurchaseAmount() {
        return purchaseAmount;
    }

    public List<Lotto> getIssuedLottos() {
        return issuedLottos;
    }

    public LocalDateTime getPurchasedAt() {
        return purchasedAt;
    }
}
