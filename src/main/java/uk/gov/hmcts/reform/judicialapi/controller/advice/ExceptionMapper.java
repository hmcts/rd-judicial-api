package uk.gov.hmcts.reform.judicialapi.controller.advice;

import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
@ControllerAdvice(basePackages = {"uk.gov.hmcts.reform.judicialapi.controller",
        "uk.gov.hmcts.reform.judicialapi.elinks"})
@RequestMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
@SuppressWarnings("checkstyle:Indentation")
public class ExceptionMapper {

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    private static final String HANDLING_EXCEPTION_TEMPLATE = "{}:: handling exception: {}";

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Object> customValidationError(
            InvalidRequestException ex) {
        return errorDetailsResponseEntity(ex, BAD_REQUEST, INVALID_REQUEST_EXCEPTION.getErrorMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> customSerializationError(
        HttpMessageNotReadableException ex) {

        String field = "";
        if (ex.getCause() != null) {
            JsonMappingException jme = (JsonMappingException) ex.getCause();
            field = jme.getPath().get(0).getFieldName();
        }
        var errorDetails = new ErrorResponse(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(),
             field + " in invalid format",
            INVALID_REQUEST_EXCEPTION.getErrorMessage(), getTimeStamp());

        return new ResponseEntity<>(errorDetails, BAD_REQUEST);
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var errors = new ArrayList<String>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + " " + error.getDefaultMessage());
        }
        var errorDetails = new ErrorResponse(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(),
                INVALID_REQUEST_EXCEPTION.getErrorMessage(), errors.toString(), getTimeStamp());

        return new ResponseEntity<>(errorDetails, BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        return errorDetailsResponseEntity(ex, INTERNAL_SERVER_ERROR, UNKNOWN_EXCEPTION.getErrorMessage());
    }

    @ExceptionHandler(UserProfileException.class)
    public ResponseEntity<Object> handleJsonFeignResponseException(UserProfileException ex) {
        ErrorResponse errorDetails = new ErrorResponse(ex.getStatus().value(),ex.getStatus().getReasonPhrase(),
                ex.getErrorMessage(), ex.getErrorDescription(), getTimeStamp());

        return new ResponseEntity<>(errorDetails, ex.getStatus());
    }

    @ExceptionHandler(ElinksException.class)
    public ResponseEntity<Object> handleElinksException(
            HttpServletRequest request,
            ElinksException e) {
        return errorDetailsResponseEntity(e, e.getStatus(), e.getErrorMessage());
    }

    private String getTimeStamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS"));
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
