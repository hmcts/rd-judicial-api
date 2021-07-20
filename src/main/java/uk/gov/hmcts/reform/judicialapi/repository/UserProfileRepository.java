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

    @Query(value = "select up from judicial_user_profile up \n"
            + "LEFT JOIN judicial_office_authorisation auth \n"
            + "ON up.per_Id = auth.per_Id \n"
            + "LEFT JOIN judicial_office_appointment appt \n"
            + "ON up.per_Id = appt.per_Id \n"
            + "where auth.service_code IN :serviceCode",nativeQuery = true)
    Page<UserProfile> fetchUserProfileByServiceNames(Set<String> serviceCode, Pageable pageable);
}
