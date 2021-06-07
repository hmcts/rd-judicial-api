package uk.gov.hmcts.reform.judicialapi.controller.controller.response;

import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.controller.response.AuthorisationResponse;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createAuthorisation;

public class AuthorisationResponseTest {

    @Test
    public void test_AuthorisationResponseTest() {
        Authorisation authorisation = createAuthorisation();
        AuthorisationResponse authorisationResponse = new AuthorisationResponse(authorisation);

        assertThat(authorisationResponse.getAuthorisationId()).isEqualTo(authorisation.getOfficeAuthId().toString());
        assertThat(authorisationResponse.getJurisdiction()).isEqualTo(authorisation.getJurisdiction());
    }

    @Test
    public void test_AuthorisationResponseSetter() {
        AuthorisationResponse authorisationResponse = new AuthorisationResponse();

        authorisationResponse.setAuthorisationId("authorisationId");
        authorisationResponse.setJurisdiction("jurisdictionId");

        assertThat(authorisationResponse.getAuthorisationId()).isEqualTo("authorisationId");
        assertThat(authorisationResponse.getJurisdiction()).isEqualTo("jurisdictionId");

    }
}
