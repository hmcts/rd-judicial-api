package uk.gov.hmcts.reform.judicialapi.controller.controller.advice;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ForbiddenException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ForbiddenExceptionTest {

    @Test
    void test_handle_forbidden_exception() {
        ForbiddenException forbiddenException = new ForbiddenException("Forbidden");
        assertNotNull(forbiddenException);
        assertEquals(HttpStatus.FORBIDDEN.getReasonPhrase(), forbiddenException.getMessage());
    }

}
