package uk.gov.hmcts.reform.judicialapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;

@Slf4j
public class RetrieveJudicialRolesTypesTest extends AuthorizationEnabledIntegrationTest {

    @Test
    public void user_with_caseworker_role_can_retrieve_judicial_role_types() {
        Map<String, Object> response = judicialReferenceDataClient.retrieveAllJudicialRoleTypes(caseworker);
        assertThat(response.get("http_status")).isEqualTo("200 OK");
    }

    //Awaiting complete S2S and IDAM setup in order to include this test
    public void user_with_non_caseworker_role_cannot_retrieve_judicial_role_types() {
        Map<String, Object> response = judicialReferenceDataClient.retrieveAllJudicialRoleTypes(puiOrgManager);
        log.info("response::::::" + response);
        assertThat(response.get("http_status")).isEqualTo("403");
    }
}
