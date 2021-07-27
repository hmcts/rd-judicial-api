package uk.gov.hmcts.reform.judicialapi;

import lombok.extern.slf4j.Slf4j;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.util.CustomSerenityRunner;
import uk.gov.hmcts.reform.judicialapi.util.FeatureConditionEvaluation;
import uk.gov.hmcts.reform.judicialapi.util.ToggleEnable;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.System.getenv;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RunWith(CustomSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
@Slf4j
public class JudicialUsersFunctionalTest extends AuthorizationFunctionalTest {

    private DataSource dataSource;
    private JdbcTemplate template;

    private static final String INSERT_MULTIPLE_USERS =
              "INSERT INTO judicial_user_profile (per_id, personal_code, appointment, known_as, surname, full_name, "
            + "ejudiciary_email, extracted_date, sidam_id) "
            + "VALUES (12344, 'Emp1', 'Magistrate', 'Test', 'Test1','Test Test1', 'abc@gmail.com', current_timestamp, "
            + "'44862987-4b00-e2e7-4ff8-281b87f16bf9') ON CONFLICT (per_id) DO NOTHING;";

    private static final String DELETE_TEST_USERS = "DELETE FROM judicial_user_profile WHERE "
            + "sidam_id IN ('44862987-4b00-e2e7-4ff8-281b87f16bf9');";

    public static final String FETCH_USERS = "JrdUsersController.fetchUsers";

    public void cleanUp() {
        template.update(DELETE_TEST_USERS);
        try {
            template.getDataSource().getConnection().close();
        } catch (Exception e) {
            log.info("DB connection not found");
        }
    }

    private void dbSetup() {
        String host = getenv("DATABASE_HOST");
        String port = getenv("DATABASE_PORT");
        String dbName = getenv("DATABASE_NAME");
        String username = getenv("DATABASE_USER");
        String password = getenv("DATABASE_PASS");
        dataSource = DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://" + host + ":" + port + "/" + dbName)
                .username(username)
                .password(password)
                .build();

        template = new JdbcTemplate(dataSource);
        template.update(INSERT_MULTIPLE_USERS);
    }

    @Test
    @ToggleEnable(mapKey = FETCH_USERS, withFeature = true)
    public void shouldReturn200whenUserProfileExistsForGivenUserId() {
        if (getenv("execution_environment").equalsIgnoreCase("aat")) {
            dbSetup();
        }
        List<OrmResponse> userProfiles = (List<OrmResponse>)
                judicialApiClient.fetchUserProfiles(getUserRequest(), 10, 0, OK,
                        ROLE_JRD_SYSTEM_USER);

        assertThat(userProfiles).isNotNull().hasSize(1);

        if (getenv("execution_environment").equalsIgnoreCase("aat")) {
            cleanUp();
        }
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

    private UserRequest getDummyUserRequest() {
        var userIds = new ArrayList<String>();
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
