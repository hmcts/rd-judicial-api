package uk.gov.hmcts.reform.judicialapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.util.List;

@Repository
public interface IdamUserProfileRepository extends JpaRepository<UserProfile, String> {

    @Override
    List<UserProfile> findAll();
}
