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
            + "and ((DATE(appt.endDate) >= CURRENT_DATE or DATE(appt.endDate) is null) "
            + "or (DATE(auth.endDate) >= CURRENT_DATE or DATE(auth.endDate) is null)) "
            + "and ( (:serviceCode is not null and (lower(appt.serviceCode) = :serviceCode "
            + ")) or :serviceCode is null ) "
            + "and ( :serviceCode = 'bfa1' or ((:locationCode is not null "
            + "and lower(appt.epimmsId) = :locationCode)"
            + " or :locationCode is null)) "
            + "and (lower(per.knownAs) like %:searchString% "
            + "or lower(per.surname) like %:searchString% "
            + "or lower(per.fullName)  like %:searchString% "
            + ")")
    List<UserProfile> findBySearchString(String searchString, String serviceCode, String locationCode);


    @Query(value = "select jup from judicial_user_profile jup \n"
            + "JOIN FETCH judicial_office_authorisation auth \n"
            + "ON jup.perId = auth.perId \n"
            + "JOIN FETCH judicial_office_appointment appt \n"
            + "ON jup.perId = appt.perId \n"
            + "LEFT JOIN FETCH judicial_role_type jrt \n"
            + "ON jup.perId = jrt.perId \n"
            + "where (jup.objectId != '' and jup.objectId is not null)  \n"
            + "and ((DATE(appt.endDate) >= CURRENT_DATE or DATE(appt.endDate) is null) \n"
            + "or (DATE(auth.endDate) >= CURRENT_DATE or DATE(auth.endDate) is null)) \n"
            + "and (jup.objectId IN :objectIds)")
    Page<UserProfile> fetchUserProfileByObjectIds(List<String> objectIds, Pageable pageable);

    @Query(value = "select jup from judicial_user_profile jup \n"
            + "JOIN FETCH judicial_office_authorisation auth \n"
            + "ON jup.perId = auth.perId \n"
            + "JOIN FETCH judicial_office_appointment appt \n"
            + "ON jup.perId = appt.perId \n"
            + "LEFT JOIN FETCH judicial_role_type jrt \n"
            + "ON jup.perId = jrt.perId \n"
            + "where (jup.objectId != '' or jup.objectId is not null)  \n"
            + "and ((DATE(appt.endDate) >= CURRENT_DATE or DATE(appt.endDate) is null) \n"
            + "or (DATE(auth.endDate) >= CURRENT_DATE or DATE(auth.endDate) is null)) \n"
            + "and (appt.serviceCode IN :ccdServiceCode or auth.ticketCode IN :ticketCode )")
    Page<UserProfile> fetchUserProfileByServiceNames(Set<String> ccdServiceCode,
                                                     List<String> ticketCode, Pageable pageable);

    @Query(value = "select jup from judicial_user_profile jup \n"
            + "JOIN FETCH judicial_office_authorisation auth \n"
            + "ON jup.perId = auth.perId \n"
            + "JOIN FETCH judicial_office_appointment appt \n"
            + "ON jup.perId = appt.perId \n"
            + "LEFT JOIN FETCH judicial_role_type jrt \n"
            + "ON jup.perId = jrt.perId \n"
            + "where (jup.objectId != '' or jup.objectId is not null)  \n"
            + "and ((DATE(appt.endDate) >= CURRENT_DATE or DATE(appt.endDate) is null) \n"
            + "or (DATE(auth.endDate) >= CURRENT_DATE or DATE(auth.endDate) is null)) \n"
            + "and (jup.sidamId IN :sidamIds)")
    Page<UserProfile> fetchUserProfileBySidamIds(List<String> sidamIds, Pageable pageable);

    @Query(value = "select jup from judicial_user_profile jup \n"
            + "JOIN FETCH judicial_office_authorisation auth \n"
            + "ON jup.perId = auth.perId \n"
            + "JOIN FETCH judicial_office_appointment appt \n"
            + "ON jup.perId = appt.perId \n"
            + "LEFT JOIN FETCH judicial_role_type jrt \n"
            + "ON jup.perId = jrt.perId \n"
            + "where (jup.objectId != '' or jup.objectId is not null)  \n"
            + "and ((DATE(appt.endDate) >= CURRENT_DATE or DATE(appt.endDate) is null) \n"
            + "or (DATE(auth.endDate) >= CURRENT_DATE or DATE(auth.endDate) is null))")
    Page<UserProfile> fetchUserProfileByAll(Pageable pageable);

    @Query(value = "select ticketCode from judicial_service_code_mapping where serviceCode IN :ccdServiceCode")
    List<String> fetchTicketCodeFromServiceCode(Set<String> ccdServiceCode);

}
