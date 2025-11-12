package io.woohyeon.lotto.lotto_web.dto.response;

import io.woohyeon.lotto.lotto_web.model.Rank;
import java.util.List;
import java.util.Map.Entry;

public record ExpectedStatistics(
        int totalSamples,
        double averageReturnRate,
        List<Entry<Rank, Long>> accumulatedRankCounts
) {
}
