package uk.gov.hmcts.reform.judicialapi;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;

@RunWith(SpringIntegrationSerenityRunner.class)
@ActiveProfiles("functional")
@Slf4j
@Ignore
public class RetrieveJudicialRolesTest extends AuthorizationFunctionalTest {

    @BeforeClass
    public static void dbSetup() throws Exception {
        String loadFile = ResourceUtils.getFile("classpath:load-data-functional.sql").getCanonicalPath();
        executeScript(ImmutableList.of(Paths.get(loadFile)));
    }

    @Test
    public void rdcc_739_ac1_user_with_appropriate_rights_can_retrieve_list_of_all_judicial_roles() {
        //Given I am a user with the appropriate rights to retrieve list of all Judicial Roles
        //When I search for the list of all Judicial Roles
        //Then I should be able to see the list of Judicial roles
        Map<String, Object> judicialRoleTypesResponse = judicialApiClient.retrieveAllJudicialRoles(caseworker, HttpStatus.OK);
        List<HashMap> judicialRoles = (List<HashMap>) judicialRoleTypesResponse.get("judicialRoles");
        judicialRoles.stream().forEach(role -> {
            assertThat("roleId").isNotNull();
            if (role.get("roleId").equals("1")) {
                assertThat(role.get("roleDescEn")).isEqualTo("Magistrate");
            } else if (role.get("roleId").equals("2")) {
                assertThat(role.get("roleDescEn")).isEqualTo("Advisory Committee Member - Magistrate");
            }
        });

    }

    //Awaiting S2S setup in order to complete and include this test
    public void rdcc_739_ac2_user_without_appropriate_rights_cannot_retrieve_list_of_all_judicial_roles() {
        //Given I am a user without the appropriate rights to retrieve list of all Judicial Roles
        //When I search for the list of all Judicial Roles
        //Then I should get a failure outcome as I do not have the appropriate rights for accessing role information
        Map<String, Object> response = judicialApiClient.retrieveAllJudicialRoles(puiOrgManager, HttpStatus.FORBIDDEN);
        log.info("response::::::" + response);
        //assertThat(response.get("errorMessage")).isNotNull();
        //assertThat(response.get("errorMessage")).isEqualTo("9 : Access Denied");
    }

    @AfterClass
    public static void dbTearDown() throws Exception {
        String deleteFile = ResourceUtils.getFile("classpath:delete-data-functional.sql").getCanonicalPath();
        executeScript(ImmutableList.of(Paths.get(deleteFile)));
    }

}
