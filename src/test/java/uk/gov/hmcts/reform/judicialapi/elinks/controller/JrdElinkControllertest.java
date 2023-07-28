package uk.gov.hmcts.reform.judicialapi.elinks.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinkUserService;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RequestUtils;

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



    /* @Test
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
    }*/

    @Test
    void shouldRefreshUserProfile() {
        responseEntity = ResponseEntity.ok().body(null);
        when(elinkUserService.refreshUserProfile(any(), any(), any(), any(), any()))
                .thenReturn(responseEntity);

        PageRequest pageRequest = RequestUtils.validateAndBuildPaginationObject(1, 0,
                "ASC", "objectId",
                20, "objectId", UserProfile.class);

        RefreshRoleRequest refreshRoleRequest = new RefreshRoleRequest("cmc", null, null, null);
        ResponseEntity<?> actual = jrdElinkController
                .refreshUserProfile(refreshRoleRequest, 1, 0,
                        "ASC", "objectId");

        assertNotNull(actual);
        verify(elinkUserService, times(1))
                .refreshUserProfile(refreshRoleRequest, 1, 0,
                        "ASC", "objectId");

    }
}
