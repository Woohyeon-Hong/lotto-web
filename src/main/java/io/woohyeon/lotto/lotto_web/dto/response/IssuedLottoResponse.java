package io.woohyeon.lotto.lotto_web.dto.response;

import io.woohyeon.lotto.lotto_web.model.Lotto;
import java.time.LocalDateTime;
import java.util.List;

public record IssuedLottoResponse(
        List<Integer> numbers,
        LocalDateTime issuedAt
) {
    public static IssuedLottoResponse of(Lotto lotto, LocalDateTime issuedAt) {
        return new IssuedLottoResponse(lotto.getNumbers(), issuedAt);
    }
}
