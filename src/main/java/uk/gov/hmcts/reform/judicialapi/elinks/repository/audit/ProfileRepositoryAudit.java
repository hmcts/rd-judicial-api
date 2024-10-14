package uk.gov.hmcts.reform.judicialapi.elinks.repository.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.audit.UserProfileAudit;

@Repository
public interface ProfileRepositoryAudit extends JpaRepository<UserProfileAudit, String> {

}
