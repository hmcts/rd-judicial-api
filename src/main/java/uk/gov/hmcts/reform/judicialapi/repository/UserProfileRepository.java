package uk.gov.hmcts.reform.judicialapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.util.List;

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
                   + "and ( (:serviceCode is not null and (lower(appt.serviceCode) = :serviceCode or "
                   + "lower(auth.serviceCode) = :serviceCode)) or :serviceCode is null ) "
                   + "and ( :serviceCode = 'bfa1' or ((:locationCode is not null "
                   + "and lower(appt.epimmsId) = :locationCode)"
                   + " or :locationCode is null)) "
                   + "and (lower(per.knownAs) like %:searchString% "
                   + "or lower(per.surname) like %:searchString% "
                   + "or lower(per.fullName)  like %:searchString% "
                   + ")")
    List<UserProfile> findBySearchString(String searchString, String serviceCode, String locationCode);
}
