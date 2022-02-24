package uk.gov.hmcts.reform.judicialapi;

import lombok.extern.slf4j.Slf4j;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.judicialapi.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.util.FeatureToggleConditionExtension;
import uk.gov.hmcts.reform.judicialapi.util.ToggleEnable;
import uk.gov.hmcts.reform.judicialapi.util.serenity5.SerenityTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.util.FeatureToggleConditionExtension.getToggledOffMessage;

@SerenityTest
@SpringBootTest
@WithTags({@WithTag("testType:Functional")})
@Slf4j
class JudicialUsersFunctionalTest extends AuthorizationFunctionalTest {

    public static final String FETCH_USERS = "JrdUsersController.fetchUsers";
    public static final String USERS_SEARCH = "JrdUsersController.searchUsers";
    public static final String REFRESH_USER = "JrdUsersController.refreshUserProfile";



    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = FETCH_USERS, withFeature = true)
    void shouldReturn_200Status(String role) {
        var response = judicialApiClient.fetchUserProfiles(getDummyUserRequest(), 10, 0,
                 NOT_FOUND, role);

        assertNotNull(response);
    }

    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = FETCH_USERS, withFeature = true)
    void shouldReturnDataNotFoundWhenUserProfilesDoNotExistForGivenUserId(String role) {
        ErrorResponse errorResponse = (ErrorResponse)
                judicialApiClient.fetchUserProfiles(getDummyUserRequest(), 10, 0, NOT_FOUND,
                        role);

        assertNotNull(errorResponse);
    }

    @Test
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = FETCH_USERS, withFeature = true)
    void shouldThrowForbiddenExceptionForNonCompliantRole() {
        ErrorResponse errorResponse = (ErrorResponse)
                judicialApiClient.fetchUserProfiles(getDummyUserRequest(), 10, 0, FORBIDDEN,
                        "prd-admin");

        assertNotNull(errorResponse);
    }

    @Test
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = FETCH_USERS, withFeature = false)
    void shouldGet403WhenApiToggledOff() {

        ErrorResponse errorResponse = (ErrorResponse)
                judicialApiClient.fetchUserProfiles(getDummyUserRequest(), 10, 0, FORBIDDEN,
                        ROLE_JRD_SYSTEM_USER);

        assertNotNull(errorResponse);
        assertEquals(getToggledOffMessage(), errorResponse.getErrorMessage());
    }

    @Test
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = USERS_SEARCH, withFeature = false)
    void shouldGet403WhenUserSearchApiToggledOff() {
        var errorResponse = (ErrorResponse)
                judicialApiClient.userSearch(getUserSearchRequest(null, null, "test"),
                        ROLE_JRD_SYSTEM_USER, FORBIDDEN);
        assertNotNull(errorResponse);
    }

    @DisplayName("Scenario: Full list of Judicial user based on page size and page number")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = REFRESH_USER, withFeature = true)
    void refreshUserProfile(String role) {

        RefreshRoleRequest refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(Collections.emptyList())
                .build();

        var response = judicialApiClient.refreshUserProfiles(refreshRoleRequest, 1, 0,
                "objectId", "ASC", NOT_FOUND, role);

        assertNotNull(response);
    }

    @DisplayName("Scenario: Full list of Judicial user is sorted based on the descending order")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = REFRESH_USER, withFeature = true)
    void refreshUserProfileSortDesc(String role) {

        RefreshRoleRequest refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("")
                .sidamIds(Collections.emptyList())
                .objectIds(Collections.emptyList())
                .build();

        var response = judicialApiClient.refreshUserProfiles(refreshRoleRequest, 1, 0,
                "objectId", "DESC", NOT_FOUND, role);

        assertNotNull(response);
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
