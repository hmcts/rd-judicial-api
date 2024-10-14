package uk.gov.hmcts.reform.judicialapi.elinks.response;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserProfileAuditRefreshResponseTest {

    @Test
    void test_AppointmentResponseSetter() {

        AppointmentRefreshResponse appointmentResponse = new AppointmentRefreshResponse();

        appointmentResponse.setAppointmentId("appointmentId");
        appointmentResponse.setBaseLocationId("baseLocationId");
        appointmentResponse.setIsPrincipalAppointment("true");
        appointmentResponse.setStartDate("01/01/2021");
        appointmentResponse.setEndDate("01/01/2021");
        appointmentResponse.setContractTypeId("contractTypeId");

        AuthorisationRefreshResponse authorisationRefreshResponse = new AuthorisationRefreshResponse();

        authorisationRefreshResponse.setAuthorisationId("authId");
        authorisationRefreshResponse.setJurisdiction("jurisdiction");
        authorisationRefreshResponse.setAppointmentId("appointmentId");
        authorisationRefreshResponse.setStartDate("01/01/2021");
        authorisationRefreshResponse.setEndDate("01/01/2021");
        authorisationRefreshResponse.setServiceCodes(List.of("AA1"));
        authorisationRefreshResponse.setTicketCode("ticketCode");
        authorisationRefreshResponse.setTicketDescription("ticketDescription");

        List<AuthorisationRefreshResponse> authorisationRefreshResponseList = new
                ArrayList<AuthorisationRefreshResponse>();
        authorisationRefreshResponseList.add(authorisationRefreshResponse);
        List<AppointmentRefreshResponse> appointmentRefreshResponseList = new ArrayList<AppointmentRefreshResponse>();
        authorisationRefreshResponseList.add(authorisationRefreshResponse);
        UserProfileRefreshResponse userProfileRefreshResponse = new UserProfileRefreshResponse();
        userProfileRefreshResponse.setAuthorisations(authorisationRefreshResponseList);
        appointmentRefreshResponseList.add(appointmentResponse);

        JudicialRoleTypeRefresh judicialRoleTypeRefresh = new JudicialRoleTypeRefresh();

        judicialRoleTypeRefresh.setTitle("title");
        judicialRoleTypeRefresh.setJurisdictionRoleId("jurisdictionRoleId");
        judicialRoleTypeRefresh.setTitle("title");
        judicialRoleTypeRefresh.setStartDate("01/01/2021");
        judicialRoleTypeRefresh.setEndDate("01/01/2021");

        List<JudicialRoleTypeRefresh> judicialRoleTypeRefreshList = new ArrayList<JudicialRoleTypeRefresh>();
        judicialRoleTypeRefreshList.add(judicialRoleTypeRefresh);

        userProfileRefreshResponse.setRoles(judicialRoleTypeRefreshList);

        userProfileRefreshResponse.setAppointments(appointmentRefreshResponseList);

        userProfileRefreshResponse.setActiveFlag("true");
        userProfileRefreshResponse.setDeletedFlag("false");
        userProfileRefreshResponse.setKnownAs("knownAs");
        userProfileRefreshResponse.setFullName("fullName");
        userProfileRefreshResponse.setKnownAs("knownAs");
        userProfileRefreshResponse.setEmailId("emailId");
        userProfileRefreshResponse.setInitials("Initials");
        userProfileRefreshResponse.setObjectId("objectId");
        userProfileRefreshResponse.setPersonalCode("personalCode");
        userProfileRefreshResponse.setPostNominals("postNominals");
        userProfileRefreshResponse.setRetirementDate("RetirementDate");
        userProfileRefreshResponse.setTitle("title");
        userProfileRefreshResponse.setSidamId("sidamId");
        userProfileRefreshResponse.setSurname("surName");

        assertThat(userProfileRefreshResponse.getActiveFlag()).isEqualTo("true");
        assertThat(userProfileRefreshResponse.getDeletedFlag()).isEqualTo("false");
        assertThat(userProfileRefreshResponse.getEmailId()).isEqualTo("emailId");
        assertThat(userProfileRefreshResponse.getKnownAs()).isEqualTo("knownAs");
        assertThat(userProfileRefreshResponse.getFullName()).isEqualTo("fullName");
        assertThat(userProfileRefreshResponse.getInitials()).isEqualTo("Initials");
        assertThat(userProfileRefreshResponse.getObjectId()).isEqualTo("objectId");
        assertThat(userProfileRefreshResponse.getPersonalCode()).isEqualTo("personalCode");
        assertThat(userProfileRefreshResponse.getPostNominals()).isEqualTo("postNominals");
        assertThat(userProfileRefreshResponse.getRetirementDate()).isEqualTo("RetirementDate");
        assertThat(userProfileRefreshResponse.getTitle()).isEqualTo("title");
        assertThat(userProfileRefreshResponse.getSidamId()).isEqualTo("sidamId");
        assertThat(userProfileRefreshResponse.getSurname()).isEqualTo("surName");
        assertThat(userProfileRefreshResponse.getAppointments()).hasSize(1);
        assertThat(userProfileRefreshResponse.getAuthorisations()).hasSize(2);
        assertThat(userProfileRefreshResponse.getRoles()).hasSize(1);
    }
}
