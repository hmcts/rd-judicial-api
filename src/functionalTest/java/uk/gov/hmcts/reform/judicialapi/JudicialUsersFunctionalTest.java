package uk.gov.hmcts.reform.judicialapi;

import lombok.extern.slf4j.Slf4j;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.util.CustomSerenityRunner;
import uk.gov.hmcts.reform.judicialapi.util.FeatureConditionEvaluation;
import uk.gov.hmcts.reform.judicialapi.util.ToggleEnable;

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.util.JrdConstant.USER_DATA_NOT_FOUND;

@RunWith(CustomSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
@Slf4j
public class JudicialUsersFunctionalTest extends AuthorizationFunctionalTest {

    public static final String FETCH_USERS = "JrdUsersController.fetchUsers";
    public static final String USERS_SEARCH = "JrdUsersController.searchUsers";

    @Test
    @ToggleEnable(mapKey = FETCH_USERS, withFeature = true)
    public void shouldReturnDataNotFoundWhenUserProfilesDoNotExistForGivenUserId() {
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

    @Test
    @ToggleEnable(mapKey = USERS_SEARCH, withFeature = true)
    public void shouldReturn404WhenUserProfileRequestedForGivenSearchStringNotFound() {
        var errorResponse = (ErrorResponse)
                judicialApiClient.userSearch(getUserSearchRequest(null, null, "invalid"),
                        ROLE_JRD_SYSTEM_USER, NOT_FOUND);
        assertThat(errorResponse).isNotNull();
        assertEquals(USER_DATA_NOT_FOUND, errorResponse.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = USERS_SEARCH, withFeature = true)
    public void shouldReturn404WhenUserProfileRequestedForGivenLocationNotFound() {
        var errorResponse = (ErrorResponse)
                judicialApiClient
                        .userSearch(getUserSearchRequest("20012", "BFA2", "Joe"),
                        ROLE_JRD_SYSTEM_USER, NOT_FOUND);
        assertThat(errorResponse).isNotNull();
        assertEquals(USER_DATA_NOT_FOUND, errorResponse.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = USERS_SEARCH, withFeature = false)
    public void shouldGet403WhenUserSearchApiToggledOff() {
        var errorResponse = (ErrorResponse)
                judicialApiClient.userSearch(getUserSearchRequest(null, null, "test"),
                        ROLE_JRD_SYSTEM_USER, FORBIDDEN);
        assertThat(errorResponse).isNotNull();
    }

    private UserRequest getDummyUserRequest() {
        var userIds = new ArrayList<String>();
        userIds.add(UUID.randomUUID().toString());
        userIds.add(UUID.randomUUID().toString());
        userIds.add(UUID.randomUUID().toString());

        return new UserRequest(userIds);
    }

    private UserSearchRequest getUserSearchRequest(String location, String serviceCode, String searchString) {
        return UserSearchRequest
                .builder()
                .searchString(searchString)
                .location(location)
                .serviceCode(serviceCode)
                .build();
    }

}
