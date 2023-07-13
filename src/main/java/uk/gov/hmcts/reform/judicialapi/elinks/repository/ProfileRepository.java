package uk.gov.hmcts.reform.judicialapi.elinks.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<UserProfile, String> {


    @Query(value = "select distinct new uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper"
        + "(per.title,per.knownAs,per.surname,per.fullName"
        + ",per.ejudiciaryEmailId,per.sidamId,per.initials"
        + ",per.postNominals,per.personalCode)"
        + "from judicialUserProfile per "
        + "LEFT JOIN FETCH judicialOfficeAppointment appt "
        + "on per.personalCode = appt.personalCode "
        + "LEFT JOIN FETCH judicialOfficeAuthorisation auth "
        + "on per.personalCode = auth.personalCode "
        + "where (per.objectId != '' and per.objectId is not null) "
        + "and ((appt.endDate >= CURRENT_DATE or appt.endDate is null) "
        + "and (auth.endDate >= CURRENT_DATE or auth.endDate is null)) "
        + "and ( (:serviceCode is not null and (lower(appt.serviceCode) = :serviceCode or "
        + "auth.ticketCode in :ticketCode)) or :serviceCode is null ) "
        + "and (( :serviceCode in :searchServiceCode) or ((:locationCode is not null "
        + "and lower(appt.epimmsId) = :locationCode)"
        + " or :locationCode is null)) "
        + "and (lower(per.knownAs) like %:searchString% "
        + "or lower(per.surname) like %:searchString% "
        + "or lower(per.fullName)  like %:searchString% "
        + ")")
    List<UserSearchResponseWrapper> findBySearchForString(String searchString, String serviceCode,
                                                          String locationCode, List<String> ticketCode,
                                                          List<String> searchServiceCode);


}
