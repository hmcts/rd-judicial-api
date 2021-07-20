import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.controller.JrdUsersController;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;
import uk.gov.hmcts.reform.judicialapi.controller.response.LrdOrgInfoServiceResponse;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.feign.LocationReferenceDataFeignClient;
import uk.gov.hmcts.reform.judicialapi.service.impl.JudicialUserServiceImpl;
import uk.gov.hmcts.reform.judicialapi.util.RequestUtils;

import java.util.List;

import static java.nio.charset.Charset.defaultCharset;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;


@RunWith(MockitoJUnitRunner.class)
public class UserProfileControllerTest {
    @InjectMocks
    private JrdUsersController jrdUsersController;

    @Mock
    JudicialUserServiceImpl judicialUserService;
    ResponseEntity<Object> responseEntity;

    @Mock
    LocationReferenceDataFeignClient locationReferenceDataFeignClient;

    @Test
    public void shouldFetchUserProfilesByServiceNames() throws JsonProcessingException {
        responseEntity = ResponseEntity.ok().body(null);
        ObjectMapper mapper = new ObjectMapper();

        LrdOrgInfoServiceResponse lrdOrgInfoServiceResponse = new LrdOrgInfoServiceResponse();
        lrdOrgInfoServiceResponse.setServiceCode("BFA1");
        lrdOrgInfoServiceResponse.setCcdServiceName("CMC");
        String body = mapper.writeValueAsString(List.of(lrdOrgInfoServiceResponse));

        when(judicialUserService.fetchUserProfileByServiceNames(any(), any()))
                .thenReturn(responseEntity);

        PageRequest pageRequest = RequestUtils.validateAndBuildPaginationObject(0, 1,
                "perId", "ASC",
                20, "id", UserProfile.class);

        when(locationReferenceDataFeignClient.getLocationRefServiceMapping(any()))
                .thenReturn(Response.builder()
                        .request(mock(Request.class)).body(body, defaultCharset()).status(201).build());

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