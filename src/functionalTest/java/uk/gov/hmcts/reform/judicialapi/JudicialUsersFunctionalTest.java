package uk.gov.hmcts.reform.judicialapi;

import lombok.extern.slf4j.Slf4j;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.UserSearchResponse;
import uk.gov.hmcts.reform.judicialapi.util.CustomSerenityRunner;
import uk.gov.hmcts.reform.judicialapi.util.FeatureConditionEvaluation;
import uk.gov.hmcts.reform.judicialapi.util.ToggleEnable;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.System.getenv;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.hmcts.reform.judicialapi.util.JrdConstant.USER_DATA_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.util.constants.SqlQueryConstant.DELETE_TEST_USERS_APPOINTMENT;
import static uk.gov.hmcts.reform.judicialapi.util.constants.SqlQueryConstant.DELETE_TEST_USERS_AUTHORISATION;
import static uk.gov.hmcts.reform.judicialapi.util.constants.SqlQueryConstant.DELETE_TEST_USERS_PROFILE;
import static uk.gov.hmcts.reform.judicialapi.util.constants.SqlQueryConstant.INSERT_JUD_OFFICE_APPOINTMENT;
import static uk.gov.hmcts.reform.judicialapi.util.constants.SqlQueryConstant.INSERT_JUD_OFFICE_AUTHORISATION;
import static uk.gov.hmcts.reform.judicialapi.util.constants.SqlQueryConstant.INSERT_JUD_USER_PROFILES;

@RunWith(CustomSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
@Slf4j
public class JudicialUsersFunctionalTest extends AuthorizationFunctionalTest {

    private static JdbcTemplate template;

    public static final String FETCH_USERS = "JrdUsersController.fetchUsers";
    public static final String USERS_SEARCH = "JrdUsersController.searchUsers";

    @BeforeClass
    public static void setUp() {
        if (getenv("execution_environment").equalsIgnoreCase("aat")) {
            String host = getenv("DATABASE_HOST");
            String port = getenv("DATABASE_PORT");
            String dbName = getenv("DATABASE_NAME");
            String username = getenv("DATABASE_USER");
            String password = getenv("DATABASE_PASS");

            DataSource dataSource = DataSourceBuilder.create()
                    .driverClassName("org.postgresql.Driver")
                    .url("jdbc:postgresql://" + host + ":" + port + "/" + dbName)
                    .username(username)
                    .password(password)
                    .build();

            template = new JdbcTemplate(dataSource);
            template.update(INSERT_JUD_USER_PROFILES);
            template.update(INSERT_JUD_OFFICE_APPOINTMENT);
            template.update(INSERT_JUD_OFFICE_AUTHORISATION);
        }
    }

    @AfterClass
    public static void cleanUp() {
        if (getenv("execution_environment").equalsIgnoreCase("aat")) {
            template.update(DELETE_TEST_USERS_APPOINTMENT);
            template.update(DELETE_TEST_USERS_AUTHORISATION);
            template.update(DELETE_TEST_USERS_PROFILE);
            try {
                template.getDataSource().getConnection().close();
            } catch (Exception e) {
                log.info("DB connection not found");
            }
        }
    }

    @Test
    @ToggleEnable(mapKey = FETCH_USERS, withFeature = true)
    public void shouldReturn200whenUserProfileExistsForGivenUserId() {
        List<OrmResponse> userProfiles = (List<OrmResponse>)
                judicialApiClient.fetchUserProfiles(getUserRequest(), 10, 0, OK,
                        ROLE_JRD_SYSTEM_USER);

        assertThat(userProfiles).isNotNull().hasSize(1);
    }

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
    public void shouldReturn200WhenUserProfileRequestedForGivenSearchString() {
        var userProfiles = (List<UserSearchResponse>)
                judicialApiClient.userSearch(getUserSearchRequest(null, null, "test"),
                        ROLE_JRD_SYSTEM_USER, OK);

        assertThat(userProfiles).isNotNull().hasSize(3);
        assertEquals("EMP528@ejudiciary.net", userProfiles.get(0).getEmailId());
        assertEquals("EMP529@ejudiciary.net", userProfiles.get(1).getEmailId());
        assertEquals("EMP530@ejudiciary.net", userProfiles.get(2).getEmailId());
    }

    @Test
    @ToggleEnable(mapKey = USERS_SEARCH, withFeature = true)
    public void shouldReturn200WhenUserProfileRequestedForGivenSearchStringAndServiceCode() {
        var userProfiles = (List<UserSearchResponse>)
                judicialApiClient.userSearch(getUserSearchRequest(null, "BFA1", "test"),
                        ROLE_JRD_SYSTEM_USER, OK);

        assertThat(userProfiles).isNotNull().hasSize(2);
        assertEquals("EMP528@ejudiciary.net", userProfiles.get(0).getEmailId());
        assertEquals("EMP530@ejudiciary.net", userProfiles.get(1).getEmailId());
    }

    @Test
    @ToggleEnable(mapKey = USERS_SEARCH, withFeature = true)
    public void shouldReturn200WhenUserProfileRequestedForGivenSearchStringAndServiceCodeAndLocation() {
        var userProfiles = (List<UserSearchResponse>)
                judicialApiClient
                        .userSearch(getUserSearchRequest("20013", "BFA2", "test"),
                        ROLE_JRD_SYSTEM_USER, OK);

        assertThat(userProfiles).isNotNull().hasSize(1);
        assertEquals("EMP528@ejudiciary.net", userProfiles.get(0).getEmailId());
    }

    @Test
    @ToggleEnable(mapKey = USERS_SEARCH, withFeature = true)
    public void shouldReturn200AndIgnoreLocationWhenServiceCodeIsBfa1() {
        var userProfiles = (List<UserSearchResponse>)
                judicialApiClient
                        .userSearch(getUserSearchRequest("20013", "BFA1", "test"),
                                ROLE_JRD_SYSTEM_USER, OK);

        assertThat(userProfiles).isNotNull().hasSize(2);
        assertEquals("EMP528@ejudiciary.net", userProfiles.get(0).getEmailId());
        assertEquals("EMP530@ejudiciary.net", userProfiles.get(1).getEmailId());
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

    private UserRequest getUserRequest() {
        List<String> userIds = new ArrayList<>();
        userIds.add("1111122223333");
        userIds.add("4c0ff6a3-8fd6-803b-301a-29d9dacccca8");
        userIds.add("4asd32m3-5hu4-l2d3-6fd1-3h4ud7wj38d7");

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
