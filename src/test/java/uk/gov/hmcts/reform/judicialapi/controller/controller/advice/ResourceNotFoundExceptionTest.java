package uk.gov.hmcts.reform.judicialapi.controller.controller.advice;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ResourceNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class ResourceNotFoundExceptionTest {

    @Test
    public void test_handle_resource_not_found_exception() {
        ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("Not Found");
        assertThat(resourceNotFoundException).isNotNull();
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), resourceNotFoundException.getMessage());
    }
}
