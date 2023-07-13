package uk.gov.hmcts.reform.judicialapi.elinks.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinkUserService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JrdElinkControllertest {

    @InjectMocks
    private JrdElinkController jrdElinkController;

    @Spy
    ElinkUserService elinkUserService;

    ResponseEntity<Object> responseEntity;

    @Test
    void shouldFetchUsersBasedOnSearch() {
        final var userSearchRequest = UserSearchRequest.builder().build();
        responseEntity = ResponseEntity.ok().body(null);
        when(elinkUserService.retrieveElinkUsers(any()))
            .thenReturn(responseEntity);

        final var actual = jrdElinkController
            .retrieveUsers(userSearchRequest);

        assertNotNull(actual);
        verify(elinkUserService, times(1))
            .retrieveElinkUsers(userSearchRequest);
    }
}
