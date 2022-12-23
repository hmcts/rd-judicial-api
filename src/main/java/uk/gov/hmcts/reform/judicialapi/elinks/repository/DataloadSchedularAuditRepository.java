package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;

import java.time.LocalDateTime;

public interface DataloadSchedularAuditRepository extends JpaRepository<ElinkDataSchedularAudit, String> {
    @Query(value = "SELECT max(scheduler_end_time) FROM dbjudicialdata.dataload_schedular_audit "
            + "WHERE scheduler_end_time < (SELECT MAX(scheduler_end_time) FROM dbjudicialdata.dataload_schedular_audit "
            + "WHERE api_name = 'Personal' AND status IN ('Success')) "
            + "AND api_name = 'Personal' AND status IN ('Success')", nativeQuery = true)
    LocalDateTime findByScheduleEndTime();

    @Query(value = "SELECT MAX(scheduler_end_time) FROM dbjudicialdata.dataload_schedular_audit "
            + " WHERE api_name = 'Personal' AND status IN ('Success')", nativeQuery = true)
    LocalDateTime findLatestSchedularEndTime();
}
