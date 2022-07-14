package uk.gov.hmcts.reform.judicialapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WelcomeControllerTest {

    private final WelcomeController welcomeController = new WelcomeController();

    @Test
    void should_return_welcome_response() {
        final ResponseEntity<String> responseEntity = welcomeController.welcome();
        final String expectedMessage = "Welcome to the Judicial API";
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertThat(
            responseEntity.getBody(),
            containsString(expectedMessage)
        );
    }
}
