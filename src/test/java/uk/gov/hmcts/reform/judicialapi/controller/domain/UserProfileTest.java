package uk.gov.hmcts.reform.judicialapi.controller.domain;

import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createUserProfile;

public class UserProfileTest {

    @Test
    public void testUserProfile() {
        UserProfile userProfile = createUserProfile();

        assertNotNull(userProfile);
        assertNotNull(userProfile.getAppointments());
        assertNotNull(userProfile.getAuthorisations());
        assertNotNull(userProfile.getJoiningDate());
        assertNotNull(userProfile.getLastWorkingDate());
        assertNotNull(userProfile.getLastLoadedDate());
        assertNotNull(userProfile.getExtractedDate());
        assertNotNull(userProfile.getCreatedDate());
        assertThat(userProfile.getElinksId()).isEqualTo("1");
        assertThat(userProfile.getPersonalCode()).isEqualTo("personalCode");
        assertThat(userProfile.getTitle()).isEqualTo("title");
        assertThat(userProfile.getKnownAs()).isEqualTo("knownAs");
        assertThat(userProfile.getSurname()).isEqualTo("surname");
        assertThat(userProfile.getFullName()).isEqualTo("name");
        assertThat(userProfile.getPostNominals()).isEqualTo("postNominals");
        assertThat(userProfile.getWorkPattern()).isEqualTo("workPattern");
        assertThat(userProfile.getEmailId()).isEqualTo("emailId");
        assertThat(userProfile.getActiveFlag()).isEqualTo(Boolean.TRUE);
        assertThat(userProfile.getObjectId()).isEqualTo("objectId");
        assertThat(userProfile.getSidamId()).isEqualTo("sidamId");
    }

}
