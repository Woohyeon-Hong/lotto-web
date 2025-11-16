package io.woohyeon.lotto.lotto_web.service.dto.response;

import io.woohyeon.lotto.lotto_web.model.PurchaseLog;
import java.time.LocalDateTime;

public record PurchaseSummaryResponse(
        long id,
        int purchaseAmount,
        int LottoCount,
        LocalDateTime purchasedAt,
        boolean hasResult,
        Double returnRate
) {
    public static PurchaseSummaryResponse from(
            PurchaseLog log,
            boolean hasResult,
            Double returnRate) {
        return new PurchaseSummaryResponse(
                log.getId(),
                log.getPurchaseAmount(),
                log.getIssuedLottos().size(),
                log.getPurchasedAt(),
                hasResult,
                returnRate
        );
    }
}
