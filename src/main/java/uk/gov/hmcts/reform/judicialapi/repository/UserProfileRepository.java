package uk.gov.hmcts.reform.judicialapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.util.List;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    List<UserProfile> findAll();

}
