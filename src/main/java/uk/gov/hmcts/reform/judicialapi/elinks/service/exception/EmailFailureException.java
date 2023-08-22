package uk.gov.hmcts.reform.judicialapi.elinks.service.exception;

public class EmailFailureException extends RuntimeException {

    public EmailFailureException(String errorMessage) {
        super(errorMessage);
    }

    public EmailFailureException(Throwable cause) {
        super(cause);
    }
}