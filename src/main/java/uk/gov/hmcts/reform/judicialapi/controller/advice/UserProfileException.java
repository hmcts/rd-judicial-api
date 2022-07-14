package uk.gov.hmcts.reform.judicialapi.controller.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class UserProfileException extends RuntimeException {

    private final HttpStatus status;
    private final String errorMessage;
    private final String errorDescription;

}
