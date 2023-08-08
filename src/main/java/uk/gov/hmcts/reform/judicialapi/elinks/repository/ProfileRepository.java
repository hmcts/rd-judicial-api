package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper;

import java.util.List;
import java.util.Set;

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
        + "and ( (:serviceCode is not null and ( "
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


    @Query(value = "select distinct per "
            + "from judicialUserProfile per "
            + "LEFT JOIN FETCH judicialOfficeAppointment appt "
            + "on per.personalCode = appt.personalCode "
            + "LEFT JOIN FETCH judicialOfficeAuthorisation auth "
            + "on appt.appointmentId = auth.appointmentId "
            + "LEFT JOIN FETCH judicial_additional_roles jrt "
            + "ON per.personalCode = jrt.personalCode "
            + "where (per.objectId != '' and per.objectId is not null) "
            + "and (per.objectId IN :objectIds)")
    Page<UserProfile> fetchUserProfileByObjectIds(List<String> objectIds, Pageable pageable);


    @Query(value = "select distinct per "
            + "from judicialUserProfile per "
            + "LEFT JOIN FETCH judicialOfficeAppointment appt "
            + "on per.personalCode = appt.personalCode "
            + "LEFT JOIN FETCH judicialOfficeAuthorisation auth "
            + "on appt.appointmentId = auth.appointmentId "
            + "LEFT JOIN FETCH judicial_additional_roles jrt "
            + "ON per.personalCode = jrt.personalCode "
            + "where (per.objectId != '' and per.objectId is not null) "
            + "and (per.sidamId IN :sidamIds)")
    Page<UserProfile> fetchUserProfileBySidamIds(List<String> sidamIds, Pageable pageable);

    @Query(value = "select distinct per "
            + "from judicialUserProfile per "
            + "LEFT JOIN FETCH judicialOfficeAppointment appt "
            + "on per.personalCode = appt.personalCode "
            + "LEFT JOIN FETCH judicialOfficeAuthorisation auth "
            + "on appt.appointmentId = auth.appointmentId "
            + "LEFT JOIN FETCH judicial_additional_roles jrt "
            + "ON per.personalCode = jrt.personalCode "
            + "where (per.objectId != '' and per.objectId is not null) "
            + "and (per.personalCode IN :personalCodes)")
    Page<UserProfile> fetchUserProfileByPersonalCodes(List<String> personalCodes, Pageable pageable);

    @Query(value = "select distinct per "
            + "from judicialUserProfile per "
            + "LEFT JOIN FETCH judicialOfficeAppointment appt "
            + "on per.personalCode = appt.personalCode "
            + "LEFT JOIN FETCH judicialOfficeAuthorisation auth "
            + "on appt.appointmentId = auth.appointmentId "
            + "LEFT JOIN FETCH judicial_additional_roles jrt "
            + "ON per.personalCode = jrt.personalCode "
            + "LEFT JOIN FETCH judicialLocationMapping jlm "
            + "ON appt.baseLocationId = jlm.judicialBaseLocationId "
            + "where (per.objectId != '' and per.objectId is not null) "
            + "and (jlm.serviceCode IN :ccdServiceCode or auth.ticketCode IN :ticketCode )")
    Page<UserProfile> fetchUserProfileByServiceNames(Set<String> ccdServiceCode,
                                                     List<String> ticketCode, Pageable pageable);

}
