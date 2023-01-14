package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;

import java.time.LocalDateTime;


@Repository
public interface ElinkSchedularAuditRepository extends JpaRepository<ElinkDataSchedularAudit, Long> {

    ElinkDataSchedularAudit findBySchedulerStartTime(LocalDateTime startTime);
}
