package uk.gov.hmcts.reform.judicialapi.elinks.service;

import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;

import java.util.List;

public interface ElinksPeopleDeleteAuditService {

    void auditPeopleDelete(List<Authorisation> authorisations,
                           List<Appointment> appointments,
                           List<JudicialRoleType> judicialRoleTypes,
                           List<UserProfile> userProfiles);

}
