package io.woohyeon.lotto.lotto_web.service;

import static io.woohyeon.lotto.lotto_web.support.LottoRules.LOTTO_PRICE;

import io.woohyeon.lotto.lotto_web.dto.request.LottoResultRequest;
import io.woohyeon.lotto.lotto_web.dto.response.ExpectedStatistics;
import io.woohyeon.lotto.lotto_web.dto.response.IssuedLotto;
import io.woohyeon.lotto.lotto_web.dto.response.LottoResultResponse;
import io.woohyeon.lotto.lotto_web.dto.response.PurchaseResponse;
import io.woohyeon.lotto.lotto_web.model.Lotto;
import io.woohyeon.lotto.lotto_web.model.PurchaseAmount;
import io.woohyeon.lotto.lotto_web.model.PurchaseLog;
import io.woohyeon.lotto.lotto_web.model.Rank;
import io.woohyeon.lotto.lotto_web.model.WinningNumbers;
import io.woohyeon.lotto.lotto_web.support.LottoGenerator;
import io.woohyeon.lotto.lotto_web.support.LottoStatistics;
import io.woohyeon.lotto.lotto_web.support.LottoStore;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.stereotype.Service;

@Service
public class LottoService {

    private final LottoStore lottoStore;

    public LottoService(LottoStore lottoStore) {
        this.lottoStore = lottoStore;
    }

    public PurchaseResponse purchaseLottosWith(int purchaseAmount) {
        LottoGenerator lottoGenerator = new LottoGenerator(new PurchaseAmount(purchaseAmount));
        List<Lotto> generatedLottos = lottoGenerator.generateLottos();
        return PurchaseResponse.from(generatedLottos);
    }


    public LottoResultResponse calculateStatisticsOf(LottoResultRequest request) {
        WinningNumbers winningNumbers = new WinningNumbers(request.lottoNumbers(), request.bonusNumber());

        List<IssuedLotto> issuedLottos = request.issuedLottos();
        List<Lotto> lottos = issuedLottos.stream().map(issuedLotto -> new Lotto(issuedLotto.numbers())).toList();

        LottoStatistics lottoStatistics = new LottoStatistics(winningNumbers, lottos,
                request.issuedLottos().size() * LOTTO_PRICE);

        lottoStatistics.compute();

        return LottoResultResponse.from(lottoStatistics);
    }

    public ExpectedStatistics getLottoExpectedStatistics() {
        List<PurchaseLog> logs = lottoStore.findRecentRecords();
        int totalSamples = logs.size();

        if (totalSamples == 0) {
            return new ExpectedStatistics(0, 0.0, List.of());
        }

        double averageReturnRate = logs.stream()
                .mapToDouble(PurchaseLog::returnRate)
                .average()
                .orElse(0.0);

        Map<Rank, Long> totals = new EnumMap<>(Rank.class);
        for (PurchaseLog log : logs) {
            for (Entry<Rank, Long> rankCount : log.rankCounts()) {
                totals.merge(rankCount.getKey(), rankCount.getValue(), Long::sum);
            }
        }

        List<Entry<Rank, Long>> accumulatedRankCounts = totals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();

        return new ExpectedStatistics(totalSamples, averageReturnRate, accumulatedRankCounts);
    }
}
