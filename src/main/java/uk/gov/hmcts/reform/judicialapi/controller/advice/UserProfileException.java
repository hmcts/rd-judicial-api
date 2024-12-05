package uk.gov.hmcts.reform.judicialapi.controller.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
@AllArgsConstructor
public class UserProfileException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final HttpStatus status;
    private final String errorMessage;
    private final String errorDescription;

}
