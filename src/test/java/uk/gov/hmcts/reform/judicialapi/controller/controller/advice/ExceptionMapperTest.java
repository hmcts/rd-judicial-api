package uk.gov.hmcts.reform.judicialapi.controller.controller.advice;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ExceptionMapper;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ForbiddenException;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.constants.ErrorConstants.UNKNOWN_EXCEPTION;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionMapperTest {

    @InjectMocks
    private ExceptionMapper exceptionMapper;

    @Mock
    MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    BindingResult bindingResult;


    @Test
    public void test_handle_invalid_request_exception() {
        InvalidRequestException invalidRequestException = new InvalidRequestException("Invalid Request");

        ResponseEntity<Object> responseEntity = exceptionMapper.customValidationError(invalidRequestException);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertEquals(invalidRequestException.getMessage(), ((ErrorResponse) responseEntity.getBody())
                .getErrorDescription());

    }

    @Test
    public void test_handle_launchDarkly_exception() {
        ForbiddenException forbiddenException = new ForbiddenException("LD Forbidden Exception");
        ResponseEntity<Object> responseEntity = exceptionMapper.handleLaunchDarklyException(forbiddenException);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals(forbiddenException.getMessage(), ((ErrorResponse)responseEntity.getBody())
                .getErrorDescription());
    }

    @Test
    public void test_handle_general_exception() {
        Exception exception = new Exception("General Exception");
        ResponseEntity<Object> responseEntity = exceptionMapper.handleException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(exception.getMessage(), ((ErrorResponse)responseEntity.getBody())
                .getErrorDescription());
        assertEquals(UNKNOWN_EXCEPTION.getErrorMessage(), ((ErrorResponse)responseEntity.getBody())
                .getErrorMessage());
    }

    @Test
    public void test_handle_forbidden_error_exception() {
        AccessDeniedException exception = new AccessDeniedException("Access Denied");

        ResponseEntity<Object> responseEntity = exceptionMapper.handleForbiddenException(exception);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals(exception.getMessage(), ((ErrorResponse)responseEntity.getBody()).getErrorDescription());

    }

    @Test
    public void test_handle_method_argument_not_valid_exception() {
        var fieldError =
                new FieldError("testObject", "testField", "testDefaultMessage");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        var responseEntity = exceptionMapper
                .handleMethodArgumentNotValidException(methodArgumentNotValidException);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    }
}
