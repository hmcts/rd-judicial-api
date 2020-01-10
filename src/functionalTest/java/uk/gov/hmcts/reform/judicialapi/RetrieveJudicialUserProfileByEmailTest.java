package uk.gov.hmcts.reform.judicialapi;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;


@RunWith(SpringIntegrationSerenityRunner.class)
@ActiveProfiles("functional")
@Slf4j
public class RetrieveJudicialUserProfileByEmailTest extends AuthorizationFunctionalTest {

//    @Test
//    public void can_find_a_user_by_their_email_address() {
//
//        String email = randomAlphabetic(10) + "@usersearch.test".toLowerCase();
//        OrganisationCreationRequest request = someMinimalOrganisationRequest()
//                .superUser(aUserCreationRequest()
//                        .firstName("some-fname")
//                        .lastName("some-lname")
//                        .email(email)
//                        .jurisdictions(OrganisationFixtures.createJurisdictions())
//                        .build())
//                .build();
//        Map<String, Object> response = professionalApiClient.createOrganisation(request);
//
//        String orgIdentifierResponse = (String) response.get("organisationIdentifier");
//        assertThat(orgIdentifierResponse).isNotEmpty();
//        request.setStatus("ACTIVE");
//        professionalApiClient.updateOrganisation(request, hmctsAdmin, orgIdentifierResponse);
//
//        Map<String, Object> searchResponse = judicialApiClient.(email.toLowerCase(), hmctsAdmin);
//
//        assertThat(searchResponse.get("firstName")).isEqualTo("some-fname");
//    }

}


