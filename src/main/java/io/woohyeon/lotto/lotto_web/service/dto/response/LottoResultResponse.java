package io.woohyeon.lotto.lotto_web.service.dto.response;

import io.woohyeon.lotto.lotto_web.model.RankCount;
import java.util.List;

public record LottoResultResponse(
        long purchaseId,
        int purchaseAmount,
        int totalPrize,
        double returnRate,
        List<RankCount> rankCounts
) { }
