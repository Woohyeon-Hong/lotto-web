package io.woohyeon.lotto.lotto_web.model;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public record RankCount(
        Rank rank,
        long count
) {
    public static RankCount fromEntry(Map.Entry<Rank, Long> entry) {
        return new RankCount(entry.getKey(), entry.getValue());
    }

    public static List<RankCount> fromEntries(List<Entry<Rank, Long>> entries) {
        return entries.stream()
                .map(RankCount::fromEntry)
                .toList();
    }
}