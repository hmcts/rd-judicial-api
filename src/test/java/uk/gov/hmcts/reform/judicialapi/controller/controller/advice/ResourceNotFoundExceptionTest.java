package uk.gov.hmcts.reform.judicialapi.controller.controller.advice;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResourceNotFoundExceptionTest {

    @Test
    void test_handle_resource_not_found_exception() {
        ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("Not Found");
        assertNotNull(resourceNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), resourceNotFoundException.getMessage());
    }
}
