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

    @Query(value = "select jup from judicial_user_profile jup \n"
            + "JOIN FETCH judicial_office_authorisation auth \n"
            + "ON jup.perId = auth.perId \n"
            + "JOIN FETCH judicial_office_appointment appt \n"
            + "ON jup.perId = appt.perId \n"
            + "where (jup.objectId != '' or jup.objectId is not null)  \n"
            + "and ((DATE(appt.endDate) >= CURRENT_DATE or DATE(appt.endDate) is null) \n"
            + "or (DATE(auth.endDate) >= CURRENT_DATE or DATE(auth.endDate) is null)) \n"
            + "and (appt.serviceCode IN :ccdServiceCode or auth.serviceCode IN :ccdServiceCode)")
    Page<UserProfile> fetchUserProfileByServiceNames(Set<String> ccdServiceCode, Pageable pageable);

    @Query(value = "select jup from judicial_user_profile jup \n"
            + "JOIN FETCH judicial_office_authorisation auth \n"
            + "ON jup.perId = auth.perId \n"
            + "JOIN FETCH judicial_office_appointment appt \n"
            + "ON jup.perId = appt.perId \n"
            + "where (jup.objectId != '' or jup.objectId is not null)  \n"
            + "and ((DATE(appt.endDate) >= CURRENT_DATE or DATE(appt.endDate) is null) \n"
            + "or (DATE(auth.endDate) >= CURRENT_DATE or DATE(auth.endDate) is null)) \n"
            + "and (jup.objectId IN :objectIds)")
    Page<UserProfile> fetchUserProfileByObjectIds(List<String> objectIds, Pageable pageable);

    @Query(value = "select jup from judicial_user_profile jup \n"
            + "JOIN FETCH judicial_office_authorisation auth \n"
            + "ON jup.perId = auth.perId \n"
            + "JOIN FETCH judicial_office_appointment appt \n"
            + "ON jup.perId = appt.perId \n"
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
            + "where (jup.objectId != '' or jup.objectId is not null)  \n"
            + "and ((DATE(appt.endDate) >= CURRENT_DATE or DATE(appt.endDate) is null) \n"
            + "or (DATE(auth.endDate) >= CURRENT_DATE or DATE(auth.endDate) is null))")
    Page<UserProfile> fetchUserProfileByAll(Pageable pageable);
}
