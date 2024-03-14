package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
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
    AuthorisationsRepository authorisationsRepository;
    @Autowired
    AppointmentsRepository appointmentsRepository;

    @Autowired
    private ElinkSchedularAuditRepository elinkSchedularAuditRepository;


    @BeforeEach
    void setUp() {
        initialize();
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
        assertEquals(2,idamResponse.size());

        List<UserProfile> userprofile = profileRepository.findAll();
        assertEquals(2, userprofile.size());
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cae", userprofile.get(1).getObjectId());

    }

    protected void cleanupData() {
        authorisationsRepository.deleteAll();
        appointmentsRepository.deleteAll();
        profileRepository.deleteAll();
        elinkSchedularAuditRepository.deleteAll();
    }
}
