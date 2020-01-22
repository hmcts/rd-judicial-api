package uk.gov.hmcts.reform.judicialapi.controller.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Month;
import org.junit.Test;

public class JudicialUserProfileEntityResponseUnitTest {


    @Test
    public void testJudicialUserProfileResponse() {

        JudicialUserProfileEntityResponse sut = new JudicialUserProfileEntityResponse();

        final String elinksId = "1";
        final String title = "title";
        final String KnownAs = "KnownAs";
        final String surname = "Surname";
        final String fullName = "fullname";
        final String postNominal = "postNominal";
        final String emailId = "somewhere@net.com";
        LocalDateTime joinedDate = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        LocalDateTime lastWorkingDate = LocalDateTime.of(2015, Month.DECEMBER, 29, 19, 30, 40);
        final boolean activeFlag = true;

        sut.setElinksId(elinksId);
        sut.setTitle(title);
        sut.setKnownAs(KnownAs);
        sut.setSurname(surname);
        sut.setFullName(fullName);
        sut.setPostNominals(postNominal);
        sut.setEmailId(emailId);
        sut.setJoiningDate(joinedDate);
        sut.setLastWorkingDate(lastWorkingDate);
        sut.setActiveFlag(activeFlag);


        assertThat(sut.getElinksId()).isEqualTo(elinksId);
        assertThat(sut.getTitle()).isEqualTo(title);
        assertThat(sut.getKnownAs()).isEqualTo(KnownAs);
        assertThat(sut.getSurname()).isEqualTo(surname);
        assertThat(sut.getFullName()).isEqualTo(fullName);
        assertThat(sut.getPostNominals()).isEqualTo(postNominal);
        assertThat(sut.getEmailId()).isEqualTo(emailId);
        assertThat(sut.getJoiningDate()).isEqualTo(joinedDate);
        assertThat(sut.getLastWorkingDate()).isEqualTo(lastWorkingDate);
        assertThat(sut.isActiveFlag()).isNotEqualTo(false);
    }
}