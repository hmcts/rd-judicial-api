package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeaversIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    private ProfileRepository profileRepository;

    @BeforeEach
    void setUp() {

    }

    @DisplayName("Elinks Leavers endpoint status verification")
    @Test
    void getLeaversUserProfile() {

        Map<String, Object> response = elinksReferenceDataClient.getLeavers();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkLeaversWrapperResponse profiles = (ElinkLeaversWrapperResponse)response.get("body");
        assertEquals("Leavers data loaded successfully", profiles.getMessage());
    }

    @DisplayName("Elinks Leavers to JRD user profile verification")
    @Test
    void verifyLeaversJrdUserProfile() {
        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        Map<String, Object> leaversResponse = elinksReferenceDataClient.getLeavers();
        assertThat(leaversResponse).containsEntry("http_status", "200 OK");
        ElinkLeaversWrapperResponse profiles = (ElinkLeaversWrapperResponse)leaversResponse.get("body");
        assertEquals("Leavers data loaded successfully", profiles.getMessage());

        List<UserProfile> userprofile = profileRepository.findAll();

        assertEquals(1, userprofile.size());
        assertEquals("0049931063", userprofile.get(0).getPersonalCode());
        assertEquals("2021-02-24", userprofile.get(0).getLastWorkingDate());
        assertEquals("false", userprofile.get(0).getActiveFlag());
        assertEquals("d01b0b59-8d68-4463-9887-535989208e27", userprofile.get(0).getObjectId());

    }
}
