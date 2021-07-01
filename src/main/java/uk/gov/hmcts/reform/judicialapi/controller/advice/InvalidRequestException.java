package uk.gov.hmcts.reform.judicialapi.controller.advice;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
