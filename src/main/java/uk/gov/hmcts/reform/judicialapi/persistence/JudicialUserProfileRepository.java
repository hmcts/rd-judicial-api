package uk.gov.hmcts.reform.judicialapi.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;

@Repository
public interface JudicialUserProfileRepository extends JpaRepository<JudicialUserProfile, String> {

    JudicialUserProfile findUserProfileByEmailAddress(String email);
}
