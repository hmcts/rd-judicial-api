import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.controller.JrdUsersController;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;
import uk.gov.hmcts.reform.judicialapi.util.RequestUtils;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class UserProfileControllerTest {
    @InjectMocks
    private JrdUsersController jrdUsersController;

    @Mock
    JudicialUserService judicialUserService;
    ResponseEntity<Object> responseEntity;

    @Test
    public void shouldFetchStaffByCcdServiceNames() {
        responseEntity = ResponseEntity.ok().body(null);
        when(judicialUserService.fetchUserProfileByServiceNames(any(), any()))
                .thenReturn(responseEntity);

        PageRequest pageRequest = RequestUtils.validateAndBuildPaginationObject(0, 1,
                "perId", "ASC",
                20, "id", UserProfile.class);

        ResponseEntity<?> actual = jrdUsersController
                .fetchUserProfileByServiceNames("cmc", 1, 0,
                        "ASC", "perId");

        assertNotNull(actual);
        verify(judicialUserService, times(1))
                .fetchUserProfileByServiceNames("cmc", pageRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void shouldThrowInvalidRequestExceptionForEmptyServiceName() {
        jrdUsersController
                .fetchUserProfileByServiceNames("", 1, 0,
                        "ASC", "caseWorkerId");
    }
}