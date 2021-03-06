package uk.gov.hmcts.reform.judicialapi.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.judicialapi.constants.ErrorConstants.ACCESS_EXCEPTION;
import static uk.gov.hmcts.reform.judicialapi.constants.ErrorConstants.EMPTY_RESULT_DATA_ACCESS;
import static uk.gov.hmcts.reform.judicialapi.constants.ErrorConstants.INVALID_REQUEST_EXCEPTION;
import static uk.gov.hmcts.reform.judicialapi.constants.ErrorConstants.UNKNOWN_EXCEPTION;


@Slf4j
@ControllerAdvice(basePackages = "uk.gov.hmcts.reform.judicialapi.controller")
@RequestMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
public class ExceptionMapper {

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    private static final String HANDLING_EXCEPTION_TEMPLATE = "{}:: handling exception: {}";

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Object> customValidationError(
            InvalidRequestException ex) {
        return errorDetailsResponseEntity(ex, BAD_REQUEST, INVALID_REQUEST_EXCEPTION.getErrorMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handlerForNoJudicialUsersFound(
            ResourceNotFoundException ex) {
        return errorDetailsResponseEntity(ex, NOT_FOUND, EMPTY_RESULT_DATA_ACCESS.getErrorMessage());
    }


    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleLaunchDarklyException(Exception ex) {
        return errorDetailsResponseEntity(ex, FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleForbiddenException(Exception ex) {
        return errorDetailsResponseEntity(ex, FORBIDDEN, ACCESS_EXCEPTION.getErrorMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingRequestParamException(Exception ex) {
        return errorDetailsResponseEntity(ex, BAD_REQUEST, INVALID_REQUEST_EXCEPTION.getErrorMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        return errorDetailsResponseEntity(ex, INTERNAL_SERVER_ERROR, UNKNOWN_EXCEPTION.getErrorMessage());
    }

    private String getTimeStamp() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS", Locale.ENGLISH).format(new Date());
    }

    private static Throwable getRootException(Throwable exception) {
        Throwable rootException = exception;
        while (nonNull(rootException.getCause())) {
            rootException = rootException.getCause();
        }
        return rootException;
    }

    public ResponseEntity<Object> errorDetailsResponseEntity(Exception ex, HttpStatus httpStatus, String errorMsg) {

        log.info(HANDLING_EXCEPTION_TEMPLATE, loggingComponentName, ex.getMessage(), ex);
        ErrorResponse errorDetails = new ErrorResponse(httpStatus.value(),httpStatus.getReasonPhrase(),errorMsg,
                getRootException(ex).getLocalizedMessage(),
                getTimeStamp());

        return new ResponseEntity<>(errorDetails, httpStatus);
    }
}
