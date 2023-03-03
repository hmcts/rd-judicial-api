package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unchecked")
class IdamElasticSearchIntegrationTest extends ElinksEnabledIntegrationTest {


    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    IdamTokenConfigProperties tokenConfigProperties;

    @Autowired
    private ElinkSchedularAuditRepository elinkSchedularAuditRepository;


    @BeforeEach
    void setUp() {

        final String clientId = "234342332";
        final String redirectUri = "http://idam-api.aat.platform.hmcts.net";
        final String authorization = "c2hyZWVkaGFyLmxvbXRlQGhtY3RzLm5ldDpITUNUUzEyMzQ=";
        final String clientAuth = "cmQteHl6LWFwaTp4eXo=";
        final String url = "http://127.0.0.1:5000";
        tokenConfigProperties.setClientId(clientId);
        tokenConfigProperties.setClientAuthorization(clientAuth);
        tokenConfigProperties.setAuthorization(authorization);
        tokenConfigProperties.setRedirectUri(redirectUri);
        tokenConfigProperties.setUrl(url);

        cleanupData();
    }

    @AfterEach
    void cleanUp() {
        cleanupData();
    }

    @DisplayName("Idam Elastic Search status")
    @Test
    void getIdamElasticSearchResponses() {

        Map<String, Object> response = elinksReferenceDataClient.getIdamElasticSearch();
        assertEquals("200 OK",response.get("http_status"));
    }

    @DisplayName("SIADM id verification")
    @Test
    void verifyPeopleJrdUserProfile() {

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");

        Map<String, Object> idamResponses = elinksReferenceDataClient.getIdamElasticSearch();
        assertEquals("200 OK",idamResponses.get("http_status"));
        List<IdamResponse> idamResponse = (ArrayList<IdamResponse>) idamResponses.get("body");
        assertEquals(1,idamResponse.size());

        List<UserProfile> userprofile = profileRepository.findAll();
        assertEquals(1, userprofile.size());
        assertEquals("6455c84c-e77d-4c4f-9759-bf4a93a8e971", userprofile.get(0).getSidamId());

    }

    private void cleanupData() {
        elinkSchedularAuditRepository.deleteAll();
    }
}
