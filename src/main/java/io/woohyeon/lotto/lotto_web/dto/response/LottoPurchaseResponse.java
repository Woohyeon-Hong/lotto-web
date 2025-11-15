package io.woohyeon.lotto.lotto_web.dto.response;

import io.woohyeon.lotto.lotto_web.model.Lotto;
import java.time.LocalDateTime;
import java.util.List;

public record LottoPurchaseResponse(
        long id,
        int purchaseAmount,
        int lottoCount,
        List<IssuedLottoResponse> lottos,
        LocalDateTime purchasedAt
) {
    public static LottoPurchaseResponse of(long id, int amount, List<Lotto> lottos, LocalDateTime createdAt) {
        return new LottoPurchaseResponse(
                id,
                amount,
                lottos.size(),
                lottos.stream()
                        .map(lotto -> IssuedLottoResponse.of(lotto, createdAt))
                        .toList(),
                createdAt
        );
    }
}
