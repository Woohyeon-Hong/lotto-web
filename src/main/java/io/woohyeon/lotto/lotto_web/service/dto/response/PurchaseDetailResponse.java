package io.woohyeon.lotto.lotto_web.service.dto.response;

import io.woohyeon.lotto.lotto_web.model.PurchaseLog;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseDetailResponse(
        long id,
        int purchaseAmount,
        int lottoCount,
        List<IssuedLottoResponse> lottos,
        LocalDateTime purchasedAt
) {
    public static PurchaseDetailResponse from(PurchaseLog log) {
        return new PurchaseDetailResponse(
                log.getId(),
                log.getPurchaseAmount(),
                log.getIssuedLottos().size(),
                IssuedLottoResponse.from(log.getIssuedLottos(), log.getPurchasedAt()),
                log.getPurchasedAt()
        );
    }
}
