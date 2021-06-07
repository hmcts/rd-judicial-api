package uk.gov.hmcts.reform.judicialapi.controller.controller.advice;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ForbiddenException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class ForbiddenExceptionTest {

    @Test
    public void test_handle_forbidden_exception() {
        ForbiddenException forbiddenException = new ForbiddenException("Forbidden");
        assertThat(forbiddenException).isNotNull();
        assertEquals(HttpStatus.FORBIDDEN.getReasonPhrase(), forbiddenException.getMessage());
    }

}
