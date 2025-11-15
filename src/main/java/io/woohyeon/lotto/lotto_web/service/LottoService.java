package io.woohyeon.lotto.lotto_web.service;

import io.woohyeon.lotto.lotto_web.dto.request.LottoPurchaseRequest;
import io.woohyeon.lotto.lotto_web.dto.response.LottoPurchaseResponse;
import io.woohyeon.lotto.lotto_web.model.Lotto;
import io.woohyeon.lotto.lotto_web.model.PurchaseAmount;
import io.woohyeon.lotto.lotto_web.model.PurchaseLog;
import io.woohyeon.lotto.lotto_web.model.ResultRecord;
import io.woohyeon.lotto.lotto_web.repository.ResultStore;
import io.woohyeon.lotto.lotto_web.support.LottoGenerator;
import io.woohyeon.lotto.lotto_web.repository.PurchaseStore;
import java.util.List;
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


    private PurchaseLog getPurchaseOrThrow(Long id) {
        return purchaseStore.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 구매 id를 찾을 수 없습니다. id = " + id));
    }

    private ResultRecord getResultOrThrow(Long purchaseId) {
        return resultStore.findByPurchaseId(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 결과 정보를 찾을 수 없습니다. purchaseId=" + purchaseId));
    }

//    public LottoResultResponse calculateStatisticsOf(LottoResultRequest request) {
//        WinningNumbers winningNumbers = new WinningNumbers(request.lottoNumbers(), request.bonusNumber());
//
//        List<IssuedLottoResponse> issuedLottoResponses = request.issuedLottoResponses();
//        List<Lotto> lottos = issuedLottoResponses.stream().map(issuedLotto -> new Lotto(issuedLotto.numbers())).toList();
//
//        LottoStatistics lottoStatistics = new LottoStatistics(winningNumbers, lottos,
//                request.issuedLottoResponses().size() * LOTTO_PRICE);
//
//        lottoStatistics.compute();
//
//        return LottoResultResponse.from(lottoStatistics);
//    }
//
//    public ExpectedStatistics getLottoExpectedStatistics() {
//        List<PurchaseLog> logs = purchaseStore.findRecentRecords();
//        int totalSamples = logs.size();
//
//        if (totalSamples == 0) {
//            return new ExpectedStatistics(0, 0.0, List.of());
//        }
//
//        double averageReturnRate = logs.stream()
//                .mapToDouble(PurchaseLog::returnRate)
//                .average()
//                .orElse(0.0);
//
//        Map<Rank, Long> totals = new EnumMap<>(Rank.class);
//        for (PurchaseLog log : logs) {
//            for (Entry<Rank, Long> rankCount : log.rankCounts()) {
//                totals.merge(rankCount.getKey(), rankCount.getValue(), Long::sum);
//            }
//        }
//
//        List<Entry<Rank, Long>> accumulatedRankCounts = totals.entrySet().stream()
//                .sorted(Map.Entry.comparingByKey())
//                .toList();
//
//        return new ExpectedStatistics(totalSamples, averageReturnRate, accumulatedRankCounts);
//    }
}
