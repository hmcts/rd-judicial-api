package uk.gov.hmcts.reform.judicialapi.elinks.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ElinksException  extends RuntimeException {

    private final HttpStatus status;
    private final String errorMessage;
    private final String errorDescription;

    public ElinksException(HttpStatus status,
                                   String errorMessage,
                                   String errorDescription) {
        super(errorMessage);
        this.status = status;
        this.errorMessage = errorMessage;
        this.errorDescription = errorDescription;
    }
}
