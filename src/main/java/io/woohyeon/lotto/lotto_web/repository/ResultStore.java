package io.woohyeon.lotto.lotto_web.repository;

import io.woohyeon.lotto.lotto_web.model.RankCount;
import io.woohyeon.lotto.lotto_web.model.ResultRecord;
import io.woohyeon.lotto.lotto_web.model.WinningNumbers;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ResultStore {

    // purchaseId를 id로 가진다.
    private final Map<Long, ResultRecord> resultRecords = new LinkedHashMap<Long, ResultRecord>();

    public Long save(
            long purchaseId,
            WinningNumbers winningNumbers,
            double returnRate,
            List<RankCount> rankCounts
    ) {
        ResultRecord record = new ResultRecord(
                purchaseId,
                winningNumbers,
                returnRate,
                rankCounts,
                LocalDateTime.now()
        );
        resultRecords.put(purchaseId, record);
        return purchaseId;
    }
    public Optional<ResultRecord> findByPurchaseId(long purchaseId) {
        return Optional.ofNullable(resultRecords.get(purchaseId));
    }

    public List<ResultRecord> findAll() {
        return new ArrayList<>(resultRecords.values());
    }
}
