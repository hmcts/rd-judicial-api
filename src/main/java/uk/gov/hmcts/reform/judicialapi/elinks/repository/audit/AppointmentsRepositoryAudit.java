package uk.gov.hmcts.reform.judicialapi.elinks.repository.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.audit.AppointmentAudit;

@Repository
public interface AppointmentsRepositoryAudit extends JpaRepository<AppointmentAudit, Long> {

}
