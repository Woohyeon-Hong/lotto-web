package io.woohyeon.lotto.lotto_web.service;

import io.woohyeon.lotto.lotto_web.model.Rank;
import io.woohyeon.lotto.lotto_web.model.RankCount;
import io.woohyeon.lotto.lotto_web.model.WinningNumbers;
import io.woohyeon.lotto.lotto_web.service.dto.request.LottoPurchaseRequest;
import io.woohyeon.lotto.lotto_web.service.dto.request.LottoResultRequest;
import io.woohyeon.lotto.lotto_web.service.dto.response.LottoPurchaseResponse;
import io.woohyeon.lotto.lotto_web.model.Lotto;
import io.woohyeon.lotto.lotto_web.model.PurchaseAmount;
import io.woohyeon.lotto.lotto_web.model.PurchaseLog;
import io.woohyeon.lotto.lotto_web.model.ResultRecord;
import io.woohyeon.lotto.lotto_web.repository.ResultStore;
import io.woohyeon.lotto.lotto_web.service.dto.response.LottoResultResponse;
import io.woohyeon.lotto.lotto_web.service.dto.response.PurchaseDetailResponse;
import io.woohyeon.lotto.lotto_web.service.dto.response.PurchaseSummaryResponse;
import io.woohyeon.lotto.lotto_web.service.dto.response.PurchasesResponse;
import io.woohyeon.lotto.lotto_web.support.LottoGenerator;
import io.woohyeon.lotto.lotto_web.repository.PurchaseStore;
import io.woohyeon.lotto.lotto_web.support.LottoRules;
import io.woohyeon.lotto.lotto_web.support.LottoStatistics;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LottoService {

    private final PurchaseStore purchaseStore;
    private final ResultStore resultStore;

    @Autowired
    public LottoService(PurchaseStore purchaseStore, ResultStore resultStore) {
        this.purchaseStore = purchaseStore;
        this.resultStore = resultStore;
    }

    public LottoPurchaseResponse purchaseLottosWith(LottoPurchaseRequest request) {
        PurchaseAmount amount = new PurchaseAmount(request.purchaseAmount());

        LottoGenerator lottoGenerator = new LottoGenerator(amount);
        List<Lotto> generatedLottos = lottoGenerator.generateLottos();

        Long savedId = purchaseStore.save(generatedLottos);
        PurchaseLog log = getPurchaseOrThrow(savedId);

        return LottoPurchaseResponse.of(
                log.getId(),
                log.getPurchaseAmount(),
                log.getIssuedLottos(),
                log.getPurchasedAt()
        );
    }

    public PurchasesResponse getPurchaseSummaries() {
        List<PurchaseLog> logs = purchaseStore.findAll();

        logs.sort(Comparator.comparing(PurchaseLog::getPurchasedAt).reversed());

        List<PurchaseSummaryResponse> summaries = logs.stream()
                .map(log -> {
                    ResultRecord record = resultStore.findByPurchaseId(log.getId())
                            .orElse(null);

                    boolean hasResult = (record != null);

                    Double returnRate = null;
                    if (record != null) returnRate = record.getReturnRate();

                    return PurchaseSummaryResponse.from(log, hasResult, returnRate);
                })
                .toList();

        return PurchasesResponse.from(summaries);
    }

    public PurchaseDetailResponse getPurchaseDetail(Long id) {
        PurchaseLog log = getPurchaseOrThrow(id);
        return PurchaseDetailResponse.from(log);
    }

    public LottoResultResponse updateResult(Long purchaseId,
                                            LottoResultRequest request) {
        PurchaseLog purchase = getPurchaseOrThrow(purchaseId);

        WinningNumbers winningNumbers = new WinningNumbers(
                request.lottoNumbers(),
                request.bonusNumber()
        );

        LottoStatistics statistics = new LottoStatistics(
                winningNumbers,
                purchase.getIssuedLottos(),
                purchase.getPurchaseAmount()
        );
        statistics.compute();

        List<Map.Entry<Rank, Long>> rankEntries = statistics.getRankCounts();
        List<RankCount> rankCounts = RankCount.fromEntries(rankEntries);

        int totalPrize = rankEntries.stream()
                .mapToInt(entry -> entry.getKey().getPrize() * entry.getValue().intValue())
                .sum();

        double roundedReturnRate = roundToScale(statistics.getRateOfReturn());

        resultStore.save(purchaseId, winningNumbers, totalPrize, roundedReturnRate, rankCounts);

        return new LottoResultResponse(purchaseId,
                purchase.getPurchaseAmount(),
                totalPrize,
                roundedReturnRate,
                rankCounts);
    }

    public LottoResultResponse getResult(Long purchaseId) {
        PurchaseLog purchaseLog = getPurchaseOrThrow(purchaseId);
        ResultRecord resultRecord = getResultOrThrow(purchaseId);

        return new LottoResultResponse(
                purchaseId,
                purchaseLog.getPurchaseAmount(),
                resultRecord.getTotalPrize(),
                resultRecord.getReturnRate(),
                resultRecord.getRankCounts()
        );
    }

    private PurchaseLog getPurchaseOrThrow(Long id) {
        return purchaseStore.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 구매 id를 찾을 수 없습니다. id = " + id));
    }

    private ResultRecord getResultOrThrow(Long purchaseId) {
        return resultStore.findByPurchaseId(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 결과 정보를 찾을 수 없습니다. purchaseId=" + purchaseId));
    }

    private double roundToScale(double value) {
        return BigDecimal.valueOf(value)
                .setScale(LottoRules.ROUNDING_SCALE, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
