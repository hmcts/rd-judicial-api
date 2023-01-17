package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
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


    @BeforeEach
    void setUp() {

    }

    @DisplayName("Idam Elastic Search status")
    @Test
    void getIdamElasticSearchResponses() {

        ResponseEntity<Object> response = elinksReferenceDataClient.getIdamElasticSearch();
        assertEquals(200,response.getStatusCode().value());
    }

    @DisplayName("SIADM id verification")
    @Test
    void verifyPeopleJrdUserProfile() {

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");

        ResponseEntity<Object> idamResponses = elinksReferenceDataClient.getIdamElasticSearch();
        assertEquals(200,idamResponses.getStatusCode().value());
        List<IdamResponse> idamResponse = (ArrayList<IdamResponse>) idamResponses.getBody();
        assertEquals(1,idamResponse.size());

        List<UserProfile> userprofile = profileRepository.findAll();
        assertEquals(1, userprofile.size());
        assertEquals("6455c84c-e77d-4c4f-9759-bf4a93a8e971", userprofile.get(0).getSidamId());

    }
}
