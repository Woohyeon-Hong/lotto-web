package io.woohyeon.lotto.lotto_web.controller;

import io.woohyeon.lotto.lotto_web.service.dto.request.LottoPurchaseRequest;
import io.woohyeon.lotto.lotto_web.service.dto.request.LottoResultRequest;
import io.woohyeon.lotto.lotto_web.service.dto.response.ExpectedStatistics;
import io.woohyeon.lotto.lotto_web.service.dto.response.LottoPurchaseResponse;
import io.woohyeon.lotto.lotto_web.service.LottoService;
import io.woohyeon.lotto.lotto_web.service.dto.response.LottoResultResponse;
import io.woohyeon.lotto.lotto_web.service.dto.response.PurchaseDetailResponse;
import io.woohyeon.lotto.lotto_web.service.dto.response.PurchasesResponse;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/lottos")
public class LottoRestController {

    private final LottoService lottoService;

    @Autowired
    public LottoRestController(LottoService lottoService) {
        this.lottoService = lottoService;
    }

    @PostMapping
    public ResponseEntity<Void> createPurchase(
            @RequestBody LottoPurchaseRequest request
            ) {
        LottoPurchaseResponse response = lottoService.purchaseLottosWith(request);

        URI locaation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(locaation).build();
    }

    @GetMapping
    public ResponseEntity<PurchasesResponse> getPurchases() {
        PurchasesResponse response = lottoService.getPurchaseSummaries();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDetailResponse> getPurchase(@PathVariable("id") long id) {
        PurchaseDetailResponse response = lottoService.getPurchaseDetail(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/result")
    public ResponseEntity<LottoResultResponse> updateResult(
            @PathVariable("id") long id,
            @RequestBody LottoResultRequest request
    ) {
        LottoResultResponse response = lottoService.updateResult(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<LottoResultResponse> getResult(@PathVariable("id") long id) {
        LottoResultResponse response = lottoService.getResult(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    public ResponseEntity<ExpectedStatistics> getStatistics() {
        ExpectedStatistics stats = lottoService.getStatistics();
        return ResponseEntity.ok(stats);
    }
}
