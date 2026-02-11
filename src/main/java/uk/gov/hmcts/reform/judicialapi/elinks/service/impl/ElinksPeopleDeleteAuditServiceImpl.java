package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.audit.AppointmentAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.audit.AuthorisationAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.audit.JudicialRoleTypeAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.audit.UserProfileAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.audit.AppointmentsRepositoryAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.audit.AuthorisationsRepositoryAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.audit.JudicialRoleTypeRepositoryAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.audit.ProfileRepositoryAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleDeleteAuditService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Setter
public class ElinksPeopleDeleteAuditServiceImpl implements ElinksPeopleDeleteAuditService {

    @Autowired
    private JudicialRoleTypeRepositoryAudit judicialRoleTypeRepositoryAudit;

    @Autowired
    private AuthorisationsRepositoryAudit authorisationsRepositoryAudit;

    @Autowired
    private AppointmentsRepositoryAudit appointmentsRepositoryAudit;

    @Autowired
    private ProfileRepositoryAudit profileRepositoryAudit;


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditPeopleDelete(List<Authorisation> authorisations,
                                  List<Appointment> appointments,
                                  List<JudicialRoleType> judicialRoleTypes,
                                  List<UserProfile> userProfiles) {
        log.info("Audit People Delete");
        saveAuthorisations(authorisations);

        saveAppointment(appointments);

        saveJudicialRoleTypes(judicialRoleTypes);

        saveUserProfiles(userProfiles);
    }

    private void saveUserProfiles(List<UserProfile> userProfiles) {
        if (userProfiles != null && !userProfiles.isEmpty()) {
            profileRepositoryAudit.saveAllAndFlush(userProfiles.stream()
                    .map(userProfile -> UserProfileAudit.builder()
                            .activeFlag(userProfile.getActiveFlag())
                            .deletedFlag(userProfile.getDeletedFlag())
                            .deletedOn(userProfile.getDeletedOn())
                            .emailId(userProfile.getEmailId())
                            .createdDate(userProfile.getCreatedDate())
                            .lastLoadedDate(userProfile.getLastLoadedDate())
                            .fullName(userProfile.getFullName())
                            .initials(userProfile.getInitials())
                            .knownAs(userProfile.getKnownAs())
                            .lastWorkingDate(userProfile.getLastWorkingDate())
                            .objectId(userProfile.getObjectId())
                            .personalCode(userProfile.getPersonalCode())
                            .postNominals(userProfile.getPostNominals())
                            .retirementDate(userProfile.getRetirementDate())
                            .sidamId(userProfile.getSidamId())
                            .surname(userProfile.getSurname())
                            .lastUpdated(userProfile.getLastUpdated())
                            .title(userProfile.getTitle()).build()).collect(Collectors.toList()));
            log.info("Audit User Profile Success");
        }
    }

    private void saveJudicialRoleTypes(List<JudicialRoleType> judicialRoleTypes) {
        if (judicialRoleTypes != null && !judicialRoleTypes.isEmpty()) {
            judicialRoleTypeRepositoryAudit.saveAllAndFlush(judicialRoleTypes.stream()
                    .map(judicialRoleType -> JudicialRoleTypeAudit
                            .builder().jurisdictionRoleId(judicialRoleType.getJurisdictionRoleId())
                            .personalCode(judicialRoleType.getPersonalCode())
                            .startDate(judicialRoleType.getStartDate())
                            .jurisdictionRoleNameId(judicialRoleType.getJurisdictionRoleNameId())
                            .roleId(judicialRoleType.getRoleId())
                            .title(judicialRoleType.getTitle())
                            .endDate(judicialRoleType.getEndDate())
                            .build()).collect(Collectors.toList()));
            log.info("Audit Judicial Role Types Success");
        }
    }

    private void saveAppointment(List<Appointment> appointments) {
        if (appointments != null && !appointments.isEmpty()) {
            appointmentsRepositoryAudit.saveAllAndFlush(appointments.stream()
                    .map(appointment -> AppointmentAudit.builder()
                            .appointmentMapping(appointment.getAppointmentMapping())
                            .appointmentType(appointment.getAppointmentType())
                            .baseLocationId(appointment.getBaseLocationId())
                            .appointmentId(appointment.getAppointmentId())
                            .contractTypeId(appointment.getContractTypeId())
                            .createdDate(appointment.getCreatedDate())
                            .epimmsId(appointment.getEpimmsId())
                            .isPrincipleAppointment(appointment.getIsPrincipleAppointment())
                            .joBaseLocationId(appointment.getJoBaseLocationId())
                            .lastLoadedDate(appointment.getLastLoadedDate())
                            .personalCode(appointment.getPersonalCode())
                            .location(appointment.getLocation())
                            .officeAppointmentId(appointment.getOfficeAppointmentId())
                            .regionId(appointment.getRegionId())
                            .roleNameId(appointment.getRoleNameId())
                            .type(appointment.getType())
                            .endDate(appointment.getEndDate())
                            .startDate(appointment.getStartDate())
                            .build()).collect(Collectors.toList()));
            log.info("Audit Appointments Success");
        }
    }

    private void saveAuthorisations(List<Authorisation> authorisations) {
        if (authorisations != null && !authorisations.isEmpty()) {
            authorisationsRepositoryAudit.saveAllAndFlush(authorisations.stream().map(authorisation ->
                            AuthorisationAudit.builder()
                                    .authorisationId(authorisation.getAuthorisationId())
                                    .appointmentId(authorisation.getAppointmentId())
                                    .createdDate(authorisation.getCreatedDate())
                                    .endDate(authorisation.getEndDate())
                                    .jurisdiction(authorisation.getJurisdiction())
                                    .jurisdictionId(authorisation.getJurisdictionId())
                                    .lastUpdated(authorisation.getLastUpdated())
                                    .lowerLevel(authorisation.getLowerLevel())
                                    .officeAuthId(authorisation.getOfficeAuthId())
                                    .personalCode(authorisation.getPersonalCode())
                                    .startDate(authorisation.getStartDate())
                                    .ticketCode(authorisation.getTicketCode())
                                    .build())
                    .collect(Collectors.toList()));
            log.info("Audit Authorisations Success");
        }
    }
}
