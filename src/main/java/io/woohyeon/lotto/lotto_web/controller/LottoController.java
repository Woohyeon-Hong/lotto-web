package io.woohyeon.lotto.lotto_web.controller;

import io.woohyeon.lotto.lotto_web.dto.request.LottoResultRequest;
import io.woohyeon.lotto.lotto_web.dto.response.LottoResultResponse;
import io.woohyeon.lotto.lotto_web.dto.response.PurchaseResponse;
import io.woohyeon.lotto.lotto_web.support.InputParser;
import io.woohyeon.lotto.lotto_web.service.LottoService;
import io.woohyeon.lotto.lotto_web.support.LottoStore;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/lottos")
public class LottoController {

    private final LottoService lottoService;
    private final LottoStore lottoStore;

    public LottoController(LottoService lottoService, LottoStore lottoStore) {
        this.lottoService = lottoService;
        this.lottoStore = lottoStore;
    }

    @GetMapping()
    public String displayPurchaseForm(Model model) {
        model.addAttribute("expectedStats", lottoService.getLottoExpectedStatistics());
        return "purchase-test";
    }

    @PostMapping
    public String purchase(@RequestParam("purchaseAmount") int purchaseAmount,
                           Model model,
                           HttpSession session) {
        PurchaseResponse purchaseResponse = lottoService.purchaseLottosWith(purchaseAmount);

        model.addAttribute("purchase", purchaseResponse);
        session.setAttribute("purchase", purchaseResponse);

        return "purchase-result-test";
    }

    @GetMapping("/winning-numbers")
    public String displayWinningForm() {
        return "winning-numbers-test";
    }


    //TODO: PRG 패턴 적용
    @PostMapping("/results")
    public String getResults(@RequestParam("lottoNumbers") String rawLottoNumbers,
                             @RequestParam("bonusNumber") int bonusNumber,
                             HttpSession session,
                             Model model) {
        PurchaseResponse purchase = (PurchaseResponse) session.getAttribute("purchase");
        if (purchase == null) return "redirect:/lottos";

        List<Integer> lottoNumbers = InputParser.parseRawNumbers(rawLottoNumbers);

        LottoResultResponse lottoResultResponse = lottoService.calculateStatisticsOf(
                new LottoResultRequest(purchase.issuedLottos(), lottoNumbers, bonusNumber));

        long historyId = lottoStore.save(purchase, lottoResultResponse);

        model.addAttribute("result", lottoResultResponse);
        model.addAttribute("purchase", purchase);
        model.addAttribute("historyId", historyId);

        return "result-test";
    }

    @GetMapping("/histories")
    public String displayRecent(Model model) {
        model.addAttribute("purchases", lottoStore.findRecentRecords());
        return "purchase-history-test";
    }
}
