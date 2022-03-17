package uk.gov.hmcts.reform.judicialapi.controller.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.controller.JrdUsersController;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;
import uk.gov.hmcts.reform.judicialapi.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;
import uk.gov.hmcts.reform.judicialapi.util.RequestUtils;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JrdUsersControllerTest {

    @InjectMocks
    private JrdUsersController jrdUsersController;

    @Mock
    JudicialUserService judicialUserServiceMock;

    ResponseEntity<Object> responseEntity;
    UserRequest userRequest;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest(Arrays.asList(
        UUID.randomUUID().toString(), UUID.randomUUID().toString()));
    }

    @Test
    void shouldFetchJudicialUsers() {
        responseEntity = ResponseEntity.ok().body(null);
        when(judicialUserServiceMock.fetchJudicialUsers(any(), any(), any()))
                .thenReturn(responseEntity);

        ResponseEntity<?> actual = jrdUsersController
                .fetchUsers(10, 0, userRequest);

        assertNotNull(actual);
        verify(judicialUserServiceMock, times(1))
                .fetchJudicialUsers(10, 0, userRequest.getUserIds());
    }

    @Test
    void shouldThrowInvalidRequestExceptionForEmptyServiceName() {
        final var userRequest = new UserRequest();
        assertThrows(InvalidRequestException.class,
            () -> jrdUsersController.fetchUsers(10, 0, userRequest));
    }

    @Test
    void shouldRetrieveUsersBasedOnSearch() {
        final var userSearchRequest = UserSearchRequest.builder().build();
        responseEntity = ResponseEntity.ok().body(null);
        when(judicialUserServiceMock.retrieveUserProfile(any()))
                .thenReturn(responseEntity);

        final var actual = jrdUsersController
                .searchUsers(userSearchRequest);

        assertNotNull(actual);
        verify(judicialUserServiceMock, times(1))
                .retrieveUserProfile(userSearchRequest);
    }

    @Test
    void shouldRefreshUserProfile() {
        responseEntity = ResponseEntity.ok().body(null);
        when(judicialUserServiceMock.refreshUserProfile(any(),any(),any(),any(), any()))
                .thenReturn(responseEntity);

        PageRequest pageRequest = RequestUtils.validateAndBuildPaginationObject(1, 0,
                "ASC", "objectId",
                20, "objectId", UserProfile.class);

        RefreshRoleRequest refreshRoleRequest = new RefreshRoleRequest("cmc", null, null,null);
        ResponseEntity<?> actual = jrdUsersController
                .refreshUserProfile(refreshRoleRequest, 1, 0,
                        "ASC", "objectId");

        assertNotNull(actual);
        verify(judicialUserServiceMock, times(1))
                .refreshUserProfile(refreshRoleRequest, 1, 0,
                        "ASC", "objectId");
    }

}
