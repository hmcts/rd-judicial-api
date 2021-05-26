package uk.gov.hmcts.reform.judicialapi.controller.advice;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
