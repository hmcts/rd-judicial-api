package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.audit.AppointmentsRepositoryAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.audit.AuthorisationsRepositoryAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.audit.JudicialRoleTypeRepositoryAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.audit.ProfileRepositoryAudit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ElinksPeopleDeleteAuditServiceImplTest {

    @Spy
    private JudicialRoleTypeRepositoryAudit judicialRoleTypeRepositoryAudit;

    @Spy
    private AuthorisationsRepositoryAudit authorisationsRepositoryAudit;

    @Spy
    private AppointmentsRepositoryAudit appointmentsRepositoryAudit;

    @Spy
    private ProfileRepositoryAudit profileRepositoryAudit;

    @InjectMocks
    private ElinksPeopleDeleteAuditServiceImpl elinksPeopleDeleteAuditService;

    @Test
    void shouldNotInvokeSaveAllWhenCollectionIsEmpty() {
        elinksPeopleDeleteAuditService.auditPeopleDelete(Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
        verify(authorisationsRepositoryAudit, times(0)).saveAllAndFlush(anyCollection());
        verify(appointmentsRepositoryAudit, times(0)).saveAllAndFlush(anyCollection());
        verify(judicialRoleTypeRepositoryAudit, times(0)).saveAllAndFlush(anyCollection());
        verify(profileRepositoryAudit, times(0)).saveAllAndFlush(anyCollection());
    }

    @Test
    void shouldNotInvokeSaveAllWhenCollectionIsNull() {
        elinksPeopleDeleteAuditService.auditPeopleDelete(null, null, null, null);
        verify(authorisationsRepositoryAudit, times(0)).saveAllAndFlush(anyCollection());
        verify(appointmentsRepositoryAudit, times(0)).saveAllAndFlush(anyCollection());
        verify(judicialRoleTypeRepositoryAudit, times(0)).saveAllAndFlush(anyCollection());
        verify(profileRepositoryAudit, times(0)).saveAllAndFlush(anyCollection());
    }

    @Test
    void auditPeopleDelete() {

        var authorisation = new Authorisation();
        authorisation.setOfficeAuthId(1L);
        authorisation.setJurisdiction("Languages");
        authorisation.setStartDate(LocalDate.now());
        authorisation.setEndDate(null);
        authorisation.setCreatedDate(LocalDateTime.now());
        authorisation.setLastUpdated(LocalDateTime.now());
        authorisation.setLowerLevel("Welsh");
        authorisation.setPersonalCode("100");
        authorisation.setTicketCode("373");
        authorisation.setAppointmentId("1");
        authorisation.setAuthorisationId("1");
        authorisation.setJurisdictionId("test");

        var appointment = new Appointment();
        appointment.setEpimmsId("1234");
        appointment.setOfficeAppointmentId(1L);
        appointment.setIsPrincipleAppointment(true);
        appointment.setStartDate(LocalDate.now());
        appointment.setEndDate(LocalDate.now());
        appointment.setCreatedDate(LocalDateTime.now());
        appointment.setLastLoadedDate(LocalDateTime.now());
        appointment.setRegionId("1");
        appointment.setPersonalCode("");
        appointment.setBaseLocationId("1");
        appointment.setAppointmentMapping("String");
        appointment.setAppointmentType("test");
        appointment.setType("test");
        appointment.setAppointmentId("1");
        appointment.setRoleNameId("test");
        appointment.setContractTypeId("test");
        appointment.setLocation("test");
        appointment.setJoBaseLocationId("test");

        var judicialRoleType = new JudicialRoleType();
        judicialRoleType.setRoleId(3);
        judicialRoleType.setTitle("Test3");
        judicialRoleType.setEndDate(LocalDateTime.now().plusDays(3));

        var userProfile = new UserProfile();
        userProfile.setPersonalCode("Emp");
        userProfile.setKnownAs("TestEmp");
        userProfile.setSurname("Test");
        userProfile.setFullName("Test1");
        userProfile.setPostNominals("Test Test1");
        userProfile.setEmailId("abc@gmail.com");
        userProfile.setLastWorkingDate(LocalDate.now());
        userProfile.setActiveFlag(false);
        userProfile.setCreatedDate(LocalDateTime.now());
        userProfile.setLastLoadedDate(LocalDateTime.now());
        userProfile.setObjectId("asd12345-0987asdas-asdas8asdas");
        userProfile.setSidamId("4c0ff6a3-8fd6-803b-301a-29d9dacccca8");

        elinksPeopleDeleteAuditService.auditPeopleDelete(Lists.newArrayList(authorisation),
                Lists.newArrayList(appointment),
                Lists.newArrayList(judicialRoleType),
                Lists.newArrayList(userProfile));
        verify(authorisationsRepositoryAudit, times(1)).saveAllAndFlush(anyCollection());
        verify(appointmentsRepositoryAudit, times(1)).saveAllAndFlush(anyCollection());
        verify(judicialRoleTypeRepositoryAudit, times(1)).saveAllAndFlush(anyCollection());
        verify(profileRepositoryAudit, times(1)).saveAllAndFlush(anyCollection());
    }
}