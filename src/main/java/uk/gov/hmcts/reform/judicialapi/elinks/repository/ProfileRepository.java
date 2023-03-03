package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;

@Repository
public interface ProfileRepository extends JpaRepository<UserProfile, String> {


}
