package io.woohyeon.lotto.lotto_web.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public record ErrorResponse(
        int status,
         String errorCode,
         String message,
         LocalDateTime timestamp
) {
    public static ErrorResponse of(HttpStatus status, String errorCode, String message) {
        return new ErrorResponse(
                status.value(),
                errorCode,
                message,
                LocalDateTime.now()
        );
    }
}
