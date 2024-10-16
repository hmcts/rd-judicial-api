package uk.gov.hmcts.reform.judicialapi.elinks.controller.response;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UserSearchResponseWrapperTest {

    @Test
    void testUserSearchResponseWrapper() {

        UserProfile userProfile = UserProfile.builder()
            .title("title")
            .knownAs("knownAs")
            .surname("surname")
            .fullName("fullname")
            .emailId("emailId")
            .sidamId("idam")
            .initials("initials")
            .postNominals("postNominals")
            .personalCode("pcode").build();

        UserSearchResponseWrapper userSearchResponseWrapper = new UserSearchResponseWrapper(userProfile);
        assertThat(userSearchResponseWrapper.getEmailId(),is(userProfile.getEmailId()));
        assertThat(userSearchResponseWrapper.getSurname(),is(userProfile.getSurname()));
        assertThat(userSearchResponseWrapper.getTitle(),is(userProfile.getTitle()));
        assertThat(userSearchResponseWrapper.getKnownAs(),is(userProfile.getKnownAs()));
        assertThat(userSearchResponseWrapper.getIdamId(),is(userProfile.getSidamId()));
        assertThat(userSearchResponseWrapper.getPostNominals(),is(userProfile.getPostNominals()));


    }
}
