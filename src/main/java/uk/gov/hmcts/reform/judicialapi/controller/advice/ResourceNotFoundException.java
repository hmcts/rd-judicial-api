package uk.gov.hmcts.reform.judicialapi.controller.advice;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
