package uk.gov.hmcts.reform.judicialapi.elinks.repository.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.audit.AuthorisationAudit;

@Repository
public interface AuthorisationsRepositoryAudit extends JpaRepository<AuthorisationAudit, Long> {

}
