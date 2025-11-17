package io.woohyeon.lotto.lotto_web.model;

import java.time.LocalDateTime;
import java.util.List;

public class ResultRecord {

    private final Long purchaseId;
    private final WinningNumbers winningNumbers;
    private final int totalPrize;
    private final double returnRate;
    private final List<RankCount> rankCounts;
    private final LocalDateTime createdAt;

    public ResultRecord(Long purchaseId, WinningNumbers winningNumbers, int totalPrize, double returnRate,
                        List<RankCount> rankCounts, LocalDateTime createdAt) {
        this.purchaseId = purchaseId;
        this.winningNumbers = winningNumbers;
        this.totalPrize = totalPrize;
        this.returnRate = returnRate;
        this.rankCounts = rankCounts;
        this.createdAt = createdAt;
    }

    public long getPurchaseId() { return purchaseId; }
    public WinningNumbers getWinningNumbers() { return winningNumbers; }

    public int getTotalPrize() {
        return totalPrize;
    }

    public double getReturnRate() { return returnRate; }
    public List<RankCount> getRankCounts() { return rankCounts; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
