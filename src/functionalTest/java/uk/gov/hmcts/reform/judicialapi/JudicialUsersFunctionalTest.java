package uk.gov.hmcts.reform.judicialapi;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;
import uk.gov.hmcts.reform.judicialapi.util.CustomSerenityRunner;
import uk.gov.hmcts.reform.judicialapi.util.FeatureConditionEvaluation;
import uk.gov.hmcts.reform.judicialapi.util.ToggleEnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RunWith(CustomSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
@Slf4j
public class JudicialUsersFunctionalTest extends AuthorizationFunctionalTest {

    private static String PATH = "/refdata/judicial/users/fetch?page_size=" + 10 + "&page_number=" + 0;
    public static final String FETCH_USERS = "JrdUsersController.fetchUsers";

    //TODO: add successful response scenario

    @Test
    @ToggleEnable(mapKey = FETCH_USERS, withFeature = true)
    public void shouldReturnDataNotFound() {
        Response fetchResponse = judicialApiClient.getMultipleAuthHeadersInternal(ROLE_JRD_SYSTEM_USER)
                .body(getUserRequest())
                .post(PATH)
                .andReturn();

        log.info("JRD get users response: {}", fetchResponse.getStatusCode());

        fetchResponse.then()
                .assertThat()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    @ToggleEnable(mapKey = FETCH_USERS, withFeature = true)
    public void shouldThrowForbiddenExceptionForNonCompliantRole() {
        Response response = judicialApiClient.getMultipleAuthHeadersInternal("prd-admin")
                .body(getUserRequest())
                .post(PATH)
                .andReturn();

        response.then()
                .assertThat()
                .statusCode(FORBIDDEN.value());
    }

    @Test
    @ToggleEnable(mapKey = FETCH_USERS, withFeature = false)
    public void shouldGet403WhenApiToggledOff() {
        String exceptionMessage = CustomSerenityRunner.getFeatureFlagName().concat(" ")
                .concat(FeatureConditionEvaluation.FORBIDDEN_EXCEPTION_LD);

        Response response = judicialApiClient.getMultipleAuthHeadersInternal(ROLE_JRD_SYSTEM_USER)
                .body(getUserRequest())
                .post(PATH)
                .andReturn();

        response.then()
                .assertThat()
                .statusCode(FORBIDDEN.value());

        assertThat(response.getBody().asString()).contains(exceptionMessage);
    }

    private UserRequest getUserRequest() {
        List<String> userIds = new ArrayList<>();
        userIds.add(UUID.randomUUID().toString());
        userIds.add(UUID.randomUUID().toString());
        userIds.add(UUID.randomUUID().toString());

        return new UserRequest(userIds);
    }

}
