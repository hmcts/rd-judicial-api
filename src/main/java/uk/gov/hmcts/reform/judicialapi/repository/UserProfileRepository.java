package uk.gov.hmcts.reform.judicialapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.util.List;
import java.util.Set;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

    Page<UserProfile> findBySidamIdIn(List<String> sidamIds, Pageable pageable);

    @Query(value = "select distinct per "
                   + "from judicial_user_profile per "
                   + "LEFT JOIN FETCH judicial_office_appointment appt "
                   + "on per.perId = appt.perId "
                   + "LEFT JOIN FETCH judicial_office_authorisation auth "
                   + "on per.perId = auth.perId "
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
    List<UserProfile> findBySearchString(String searchString, String serviceCode, String locationCode,
                                         List<String> ticketCode, List<String> searchServiceCode);

    @Query(value = "select distinct per "
            + "from judicial_user_profile per "
            + "LEFT JOIN FETCH judicial_office_appointment appt "
            + "on per.perId = appt.perId "
            + "LEFT JOIN FETCH judicial_office_authorisation auth "
            + "on per.perId = auth.perId "
            + "LEFT JOIN FETCH judicial_role_type jrt "
            + "ON per.perId = jrt.perId "
            + "where (per.objectId != '' and per.objectId is not null) "
            + "and ((appt.endDate >= CURRENT_DATE or appt.endDate is null) "
            + "or (auth.endDate >= CURRENT_DATE or auth.endDate is null)) "
            + "and (per.objectId IN :objectIds)")
    Page<UserProfile> fetchUserProfileByObjectIds(List<String> objectIds, Pageable pageable);

    @Query(value = "select distinct per "
            + "from judicial_user_profile per "
            + "LEFT JOIN FETCH judicial_office_appointment appt "
            + "on per.perId = appt.perId "
            + "LEFT JOIN FETCH judicial_office_authorisation auth "
            + "on per.perId = auth.perId "
            + "LEFT JOIN FETCH judicial_role_type jrt "
            + "ON per.perId = jrt.perId "
            + "where (per.objectId != '' and per.objectId is not null) "
            + "and ((appt.endDate >= CURRENT_DATE or appt.endDate is null) "
            + "or (auth.endDate >= CURRENT_DATE or auth.endDate is null)) "
            + "and (appt.serviceCode IN :ccdServiceCode or auth.ticketCode IN :ticketCode )")
    Page<UserProfile> fetchUserProfileByServiceNames(Set<String> ccdServiceCode,
                                                     List<String> ticketCode, Pageable pageable);

    @Query(value = "select distinct per "
            + "from judicial_user_profile per "
            + "LEFT JOIN FETCH judicial_office_appointment appt "
            + "on per.perId = appt.perId "
            + "LEFT JOIN FETCH judicial_office_authorisation auth "
            + "on per.perId = auth.perId "
            + "LEFT JOIN FETCH judicial_role_type jrt "
            + "ON per.perId = jrt.perId "
            + "where (per.objectId != '' and per.objectId is not null) "
            + "and ((appt.endDate >= CURRENT_DATE or appt.endDate is null) "
            + "or (auth.endDate >= CURRENT_DATE or auth.endDate is null)) "
            + "and (per.sidamId IN :sidamIds)")
    Page<UserProfile> fetchUserProfileBySidamIds(List<String> sidamIds, Pageable pageable);

    @Query(value = "select distinct per "
            + "from judicial_user_profile per "
            + "LEFT JOIN FETCH judicial_office_appointment appt "
            + "on per.perId = appt.perId "
            + "LEFT JOIN FETCH judicial_office_authorisation auth "
            + "on per.perId = auth.perId "
            + "LEFT JOIN FETCH judicial_role_type jrt "
            + "ON per.perId = jrt.perId "
            + "where (per.objectId != '' and per.objectId is not null) "
            + "and ((appt.endDate >= CURRENT_DATE or appt.endDate is null) "
            + "or (auth.endDate >= CURRENT_DATE or auth.endDate is null)) "
            + "and (per.personalCode IN :personalCodes)")
    Page<UserProfile> fetchUserProfileByPersonalCodes(List<String> personalCodes, Pageable pageable);


    @Query(value = "select distinct per "
            + "from judicial_user_profile per "
            + "LEFT JOIN FETCH judicial_office_appointment appt "
            + "on per.perId = appt.perId "
            + "LEFT JOIN FETCH judicial_office_authorisation auth "
            + "on per.perId = auth.perId "
            + "LEFT JOIN FETCH judicial_role_type jrt "
            + "ON per.perId = jrt.perId "
            + "where (per.objectId != '' and per.objectId is not null) "
            + "and ((appt.endDate >= CURRENT_DATE or appt.endDate is null) "
            + "and (auth.endDate >= CURRENT_DATE or auth.endDate is null)) ")
    Page<UserProfile> fetchUserProfileByAll(Pageable pageable);

}