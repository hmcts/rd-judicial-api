package uk.gov.hmcts.reform.judicialapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.util.List;


@Repository
public interface IdamUserProfileRepository extends JpaRepository<UserProfile, String> {

    @Query(value = "SELECT per FROM judicial_user_profile per WHERE per.objectId IS NOT NULL AND  "
            + "TRIM(per.objectId) != ''")
    public List<UserProfile>  findAllJudicialUserProfiles();

}
