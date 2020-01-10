package uk.gov.hmcts.reform.judicialapi.domain;

import static com.microsoft.applicationinsights.core.dependencies.apachecommons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class JudicialUserProfileTest {

    @Test
    public void judicialUserProfileTest() {


        LocalDateTime createdDate = LocalDateTime.of(2015, Month.APRIL, 29, 19, 30, 40);
        LocalDateTime joinedDate = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        LocalDateTime extractedDate = LocalDateTime.of(2015, Month.AUGUST, 29, 19, 30, 40);
        LocalDateTime lastWorkingDate = LocalDateTime.of(2015, Month.DECEMBER, 29, 19, 30, 40);
        LocalDateTime lastLoadedDate = LocalDateTime.of(2015, Month.DECEMBER, 29, 19, 30, 40);

        String email = randomAlphabetic(10) + "@usersearch.test".toLowerCase();

        JudicialUserProfile judicialUserProfile1 = new JudicialUserProfile();

        JudicialUserProfile judicialUserProfileTest = new JudicialUserProfile("12345","54621","judge","KnownAs",
                "surName", "fullName","postNominal","contractType","workPattern",
                email,joinedDate,lastWorkingDate,true,extractedDate,createdDate,lastLoadedDate);

        List<JudicialOfficeAppointment> judicialOfficeAppointments = new ArrayList<>();
        JudicialOfficeAppointment judicialOfficeAppointment = new JudicialOfficeAppointment();

        judicialOfficeAppointments.add(judicialOfficeAppointment);

        List<JudicialOfficeAuthorisation> judicialOfficeAuthorisations = new ArrayList<>();
        JudicialOfficeAuthorisation judicialOfficeAuthorisation = new JudicialOfficeAuthorisation();

        judicialOfficeAuthorisations.add(judicialOfficeAuthorisation);

        assertThat(judicialUserProfileTest.getElinksId()).isEqualTo("12345");
        assertThat(judicialUserProfileTest.getPersonalCode()).isEqualTo("54621");
        assertThat(judicialUserProfileTest.getContractType()).isEqualTo("contractType");
        assertThat(judicialUserProfileTest.getFullName()).isEqualTo("fullName");
        assertThat(judicialUserProfileTest.getSurname()).isEqualTo("surName");
        assertThat(judicialUserProfileTest.getJoiningDate()).isEqualTo(joinedDate);
        assertThat(judicialUserProfileTest.getCreatedDate()).isEqualTo(createdDate);
        assertThat(judicialUserProfileTest.getLastLoadedDate()).isEqualTo(lastLoadedDate);
        assertThat(judicialUserProfileTest.getLastWorkingDate()).isEqualTo(lastWorkingDate);
        assertThat(judicialUserProfileTest.getExtractedDate()).isEqualTo(extractedDate);
        assertThat(judicialUserProfileTest.getEmailId()).isEqualTo(email);
        assertThat(judicialUserProfileTest.getKnownAs()).isEqualTo("KnownAs");
        assertThat(judicialUserProfileTest.getPostNominals()).isEqualTo("postNominal");
        assertThat(judicialUserProfileTest.getTitle()).isEqualTo("judge");
        assertThat(judicialUserProfileTest.getWorkPattern()).isEqualTo("workPattern");

    }

}