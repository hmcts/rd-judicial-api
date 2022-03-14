package uk.gov.hmcts.reform.judicialapi.controller.domain;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createUserProfile;

class UserProfileTest {

    @Test
    void testUserProfile() {
        UserProfile userProfile = createUserProfile();

        assertNotNull(userProfile);
        assertNotNull(userProfile.getAppointments());
        assertNotNull(userProfile.getAuthorisations());
        assertNotNull(userProfile.getJoiningDate());
        assertNotNull(userProfile.getLastWorkingDate());
        assertNotNull(userProfile.getLastLoadedDate());
        assertNotNull(userProfile.getExtractedDate());
        assertNotNull(userProfile.getCreatedDate());
        assertEquals("1", userProfile.getPerId());
        assertEquals("personalCode", userProfile.getPersonalCode());
        assertEquals("knownAs", userProfile.getKnownAs());
        assertEquals("surname", userProfile.getSurname());
        assertEquals("name", userProfile.getFullName());
        assertEquals("postNominals", userProfile.getPostNominals());
        assertEquals("workPattern", userProfile.getWorkPattern());
        assertEquals("emailId", userProfile.getEjudiciaryEmailId());
        assertEquals(Boolean.TRUE, userProfile.getActiveFlag());
        assertEquals("objectId", userProfile.getObjectId());
        assertEquals("sidamId", userProfile.getSidamId());
        assertEquals(Boolean.TRUE, userProfile.getIsJudge());
        assertEquals(Boolean.TRUE, userProfile.getIsMagistrate());
        assertEquals(Boolean.TRUE, userProfile.getIsPanelMember());
    }

}
