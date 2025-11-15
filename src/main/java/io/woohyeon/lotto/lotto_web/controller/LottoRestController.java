package io.woohyeon.lotto.lotto_web.controller;

import io.woohyeon.lotto.lotto_web.dto.request.LottoPurchaseRequest;
import io.woohyeon.lotto.lotto_web.dto.response.LottoPurchaseResponse;
import io.woohyeon.lotto.lotto_web.service.LottoService;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<LottoPurchaseResponse> createPurchase(
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
}
