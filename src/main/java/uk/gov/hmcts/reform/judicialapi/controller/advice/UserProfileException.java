package uk.gov.hmcts.reform.judicialapi.controller.advice;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserProfileException extends RuntimeException {

    private final HttpStatus status;
    private final String errorMessage;
    private final String errorDescription;

    public UserProfileException(HttpStatus status,
                                String errorMessage,
                                String errorDescription) {
        super(errorMessage);
        this.status = status;
        this.errorMessage = errorMessage;
        this.errorDescription = errorDescription;
    }
}
