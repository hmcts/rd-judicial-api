package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface ProfileRepository extends JpaRepository<UserProfile, String> {


    @Query("select distinct new uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper"
        + "(per.title,per.knownAs,per.surname,per.fullName"
        + ",per.emailId,per.sidamId,per.initials"
        + ",per.postNominals,per.personalCode)"
        + "from judicialUserProfile per "
        + "LEFT JOIN FETCH judicialOfficeAppointment appt "
        + "on per.personalCode = appt.personalCode "
        + "LEFT JOIN FETCH judicialOfficeAuthorisation auth "
        + "on auth.appointmentId = appt.appointmentId "
        + "LEFT JOIN FETCH judicialLocationMapping loc "
        + "on loc.judicialBaseLocationId = appt.baseLocationId "
        + "where (per.objectId != '' and per.objectId is not null) "
        + "and (per.activeFlag = true) "
        + "and ((appt.endDate >= CURRENT_DATE or appt.endDate is null) "
        + "and (auth.endDate >= CURRENT_DATE or auth.endDate is null)) "
        + "and ( (:serviceCode is not null and (lower(loc.serviceCode) = :serviceCode or "
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


    @Query("select distinct per "
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


    @Query("select distinct per "
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

    @Query("select distinct per "
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

    @Query("select distinct per "
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

    @Query("select distinct per "
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
            + "and (auth.ticketCode IN :ticketCode )")
    Page<UserProfile> fetchUserProfileByTicketCodes(List<String> ticketCode, Pageable pageable);


    @Query("select distinct per.objectId "
            + "from judicialUserProfile per "
            + "where (per.objectId != '' and per.objectId is not null)")
    List<String> fetchObjectId();

    @Query("select per "
            + "from judicialUserProfile per "
            + "where (per.objectId != '' and per.objectId is not null) "
            + "and (per.objectId IN :objectIds)")
    List<UserProfile> fetchUserProfiles(List<String> objectIds);

    @Query("select per "
            + "from judicialUserProfile per "
            + "where (per.objectId != '' and per.objectId is not null)"
            + "and per.sidamId is null ")
    List<UserProfile> fetchObjectIdMissingSidamId();


    List<UserProfile> findByDeletedOnBeforeAndDeletedFlag(LocalDateTime deletedDateOn, Boolean deletedFlag);

    List<UserProfile> findByDeletedFlag(Boolean deletedFlag);

    void deleteByDeletedOnBeforeAndDeletedFlag(LocalDateTime deletedDateOn,Boolean deletedFlag);

    List<UserProfile> deleteByPersonalCodeIn(List<String> personalCodes);

    List<UserProfile> findByPersonalCodeIn(List<String> personalCodes);

    @Query(value ="""
        SELECT DISTINCT jup.sidam_id 
        FROM dbjudicialdata.judicial_user_profile jup 
        LEFT JOIN dbjudicialdata.judicial_office_appointment joa
        ON joa.personal_code = jup.personal_code 
        LEFT JOIN dbjudicialdata.judicial_office_authorisation joa2
        ON joa2.personal_code = jup.personal_code 
        LEFT JOIN dbjudicialdata.judicial_additional_roles jar
        ON jar.personal_code = jup.personal_code 
        WHERE jup.sidam_id IS NOT NULL
        AND(jup.last_loaded_date >= :lastPublishedDate
        OR jar.end_date BETWEEN :lastPublishedDateAdjusted AND now()
        OR joa.end_date BETWEEN :lastPublishedDateAdjusted AND now()
        OR joa2.end_date BETWEEN :lastPublishedDateAdjusted AND now())
        """, nativeQuery = true)
    List<String> fetchDeltaLoadIdamIds(@Param("lastPublishedDate")LocalDateTime lastPublishedDate,
                                       @Param("lastPublishedDateAdjusted")LocalDateTime lastPublishedDateAdjusted);


}
