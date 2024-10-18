package uk.gov.hmcts.reform.judicialapi.controller.advice;

import java.io.Serial;

public class InvalidRequestException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidRequestException(String message) {
        super(message);
    }
}
