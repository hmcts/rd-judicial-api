package uk.gov.hmcts.reform.judicialapi;

import lombok.extern.slf4j.Slf4j;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.jdbc.Sql;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.util.CustomSerenityRunner;
import uk.gov.hmcts.reform.judicialapi.util.FeatureConditionEvaluation;
import uk.gov.hmcts.reform.judicialapi.util.ToggleEnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@RunWith(CustomSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
@Slf4j
public class JudicialUsersFunctionalTest extends AuthorizationFunctionalTest {

    public static final String FETCH_USERS = "JrdUsersController.fetchUsers";

    @Test
    @Sql("insert_user_profiles.sql")
    @Sql(scripts = "delete_user_profiles.sql", executionPhase = AFTER_TEST_METHOD)
    @ToggleEnable(mapKey = FETCH_USERS, withFeature = true)
    public void shouldReturn200() {
        List<OrmResponse> userProfiles = (List<OrmResponse>)
                judicialApiClient.fetchUserProfiles(getUserRequest(), 10, 0, OK,
                        ROLE_JRD_SYSTEM_USER);

        assertThat(userProfiles).isNotNull().hasSize(3);
    }

    @Test
    @ToggleEnable(mapKey = FETCH_USERS, withFeature = true)
    public void shouldReturnDataNotFound() {
        ErrorResponse errorResponse = (ErrorResponse)
                judicialApiClient.fetchUserProfiles(getDummyUserRequest(), 10, 0, NOT_FOUND,
                        ROLE_JRD_SYSTEM_USER);

        assertThat(errorResponse).isNotNull();
    }

    @Test
    @ToggleEnable(mapKey = FETCH_USERS, withFeature = true)
    public void shouldThrowForbiddenExceptionForNonCompliantRole() {
        ErrorResponse errorResponse = (ErrorResponse)
                judicialApiClient.fetchUserProfiles(getDummyUserRequest(), 10, 0, FORBIDDEN,
                        "prd-admin");

        assertThat(errorResponse).isNotNull();
    }

    @Test
    @ToggleEnable(mapKey = FETCH_USERS, withFeature = false)
    public void shouldGet403WhenApiToggledOff() {
        String exceptionMessage = CustomSerenityRunner.getFeatureFlagName().concat(" ")
                .concat(FeatureConditionEvaluation.FORBIDDEN_EXCEPTION_LD);

        ErrorResponse errorResponse = (ErrorResponse)
                judicialApiClient.fetchUserProfiles(getDummyUserRequest(), 10, 0, FORBIDDEN,
                        ROLE_JRD_SYSTEM_USER);

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getErrorMessage()).isEqualTo(exceptionMessage);
    }

    private UserRequest getDummyUserRequest() {
        List<String> userIds = new ArrayList<>();
        userIds.add(UUID.randomUUID().toString());
        userIds.add(UUID.randomUUID().toString());
        userIds.add(UUID.randomUUID().toString());

        return new UserRequest(userIds);
    }

    private UserRequest getUserRequest() {
        List<String> userIds = new ArrayList<>();
        userIds.add("44862987-4b00-e2e7-4ff8-281b87f16bf9");
        userIds.add("4c0ff6a3-8fd6-803b-301a-29d9dacccca8");
        userIds.add("4asd32m3-5hu4-l2d3-6fd1-3h4ud7wj38d7");

        return new UserRequest(userIds);
    }

}
