package uk.gov.hmcts.reform.judicialapi.elinks.controller.advice;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }

}
