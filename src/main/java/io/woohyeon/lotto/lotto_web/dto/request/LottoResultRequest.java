package io.woohyeon.lotto.lotto_web.dto.request;

import io.woohyeon.lotto.lotto_web.dto.response.IssuedLottoResponse;
import java.util.List;

public record LottoResultRequest(
        List<IssuedLottoResponse> issuedLottoResponses,
        List<Integer> lottoNumbers,
        int bonusNumber
) {
}
