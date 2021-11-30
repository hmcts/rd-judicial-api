
package uk.gov.hmcts.reform.judicialapi.controller.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.controller.WelcomeController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WelcomeControllerTest {

    private final WelcomeController welcomeController = new WelcomeController();

    @Test
    void test_should_return_welcome_response() throws Exception {

        ResponseEntity<String> responseEntity = welcomeController.welcome();
        final String expectedMessage = "Welcome to the Judicial API";

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertThat(responseEntity.getBody()).contains(expectedMessage);
    }
}

