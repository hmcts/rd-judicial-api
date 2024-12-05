package uk.gov.hmcts.reform.judicialapi;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserProfileRefreshResponse;
import uk.gov.hmcts.reform.judicialapi.util.FeatureToggleConditionExtension;
import uk.gov.hmcts.reform.judicialapi.util.ToggleEnable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(SerenityJUnit5Extension.class)
@SpringBootTest
@WithTags({@WithTag("testType:Functional")})
@Slf4j
class JudicialUsersFunctionalTest extends AuthorizationFunctionalTest {

    public static final String USERS_SEARCH = "JrdElinkController.retrieveUsers";
    public static final String REFRESH_USER = "JrdElinkController.refreshUserProfile";

    @Test
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = USERS_SEARCH, withFeature = false)
    void shouldGet403WhenUserSearchApiToggledOff() {
        var errorResponse = (ErrorResponse)
                judicialApiClient.userSearch(getUserSearchRequest(null, null, "test"),
                        ROLE_JRD_SYSTEM_USER, FORBIDDEN);
        assertNotNull(errorResponse);
    }

    @DisplayName("Scenario: Get Bad Request when ccdServiceName is ALL")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = REFRESH_USER, withFeature = true)
    void refreshUserProfile(String role) {

        RefreshRoleRequest refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("ALL")
                .sidamIds(Collections.emptyList())
                .objectIds(Collections.emptyList())
                .build();

        Response refreshResponse = judicialApiClient.refreshUserProfiles(refreshRoleRequest, 1, 1,
                "objectId", "ASC",role);

        assertEquals(BAD_REQUEST.value(),refreshResponse.getStatusCode());
    }

    @DisplayName("Scenario: Get Bad Request when input params are empty")
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

        Response refreshResponse = judicialApiClient.refreshUserProfiles(refreshRoleRequest, 1, 0,
                "objectId", "DESC",role);

        assertEquals(BAD_REQUEST.value(),refreshResponse.getStatusCode());
    }

    @DisplayName("Scenario: Get Judicial user based on page size and page number")
    @ParameterizedTest
    @ValueSource(strings = {"jrd-system-user", "jrd-admin"})
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = REFRESH_USER, withFeature = true)
    void refreshUserProfileGet(String role) {

        RefreshRoleRequest refreshRoleRequest = RefreshRoleRequest.builder()
                .ccdServiceNames("ia")
                .sidamIds(Collections.emptyList())
                .objectIds(Collections.emptyList())
                .build();

        Response refreshResponse = judicialApiClient.refreshUserProfiles(refreshRoleRequest, 1, 1,
                "objectId", "ASC", role);

        if (OK.value() == refreshResponse.getStatusCode()) {
            List<UserProfileRefreshResponse> userProfiles = InvokerHelper.asList(refreshResponse.getBody()
                    .as(UserProfileRefreshResponse[].class));
            log.info("JRD get refreshResponse response: {}", userProfiles.get(0).getObjectId());
            assertNotNull(userProfiles.get(0).getObjectId());
        } else {
            assertEquals(NOT_FOUND.value(), refreshResponse.getStatusCode());
        }
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
