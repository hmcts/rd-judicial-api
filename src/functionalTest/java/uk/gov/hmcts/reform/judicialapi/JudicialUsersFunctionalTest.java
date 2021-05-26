package uk.gov.hmcts.reform.judicialapi;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.util.CustomSerenityRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

@RunWith(CustomSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
@Slf4j
public class JudicialUsersFunctionalTest extends AuthorizationFunctionalTest{

    @Test
   // @ToggleEnable(mapKey = OrmFetchUsers, withFeature = true)
    public void fetchJudicialUserProfilesWithAppointmentsAndAuthorisations() {

        List searchResponse = fetchUserProfiles(getUserRequest(), 100, 0, OK.value());

//        List<CaseWorkersProfileCreationRequest> caseWorkersProfileCreationRequests = caseWorkerApiClient
//                .createCaseWorkerProfiles();
//
//        Response response = caseWorkerApiClient.createUserProfiles(caseWorkersProfileCreationRequests);
//
//        List<String> caseWorkerIds = caseWorkerProfileCreationResponse.getCaseWorkerIds();
//        assertEquals(caseWorkersProfileCreationRequests.size(), caseWorkerIds.size());
    }

    private UserRequest getUserRequest() {
        List<String> userIds = new ArrayList<>();
        userIds.add(UUID.randomUUID().toString());
        userIds.add(UUID.randomUUID().toString());
        userIds.add(UUID.randomUUID().toString());

        return new UserRequest(userIds);
    }

}
