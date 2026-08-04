package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;

import java.time.LocalDateTime;

@Repository
public interface DataloadSchedularAuditRepository extends JpaRepository<ElinkDataSchedularAudit, Long> {

    @Query(value = "SELECT MAX(scheduler_end_time) FROM dbjudicialdata.dataload_schedular_audit "
            + " WHERE api_name = 'People'  AND status IN ('SUCCESS','PARTIAL_SUCCESS')", nativeQuery = true)
    LocalDateTime findLatestSchedularEndTime();

    @Query(value = "SELECT MAX(scheduler_end_time) FROM dbjudicialdata.dataload_schedular_audit "
        + " WHERE api_name = 'Leavers'  AND status IN ('SUCCESS','PARTIAL_SUCCESS')", nativeQuery = true)
    LocalDateTime findLatestSchedularEndTimeForLeavers();

    @Query(value = "SELECT MAX(scheduler_end_time) FROM dbjudicialdata.dataload_schedular_audit "
        + " WHERE api_name = 'Deleted'  AND status IN ('SUCCESS','PARTIAL_SUCCESS')", nativeQuery = true)
    LocalDateTime findLatestDeletedSchedularEndTime();


    @Query(value = "SELECT max(scheduler_end_time) FROM dbjudicialdata.dataload_schedular_audit "
            + "WHERE scheduler_end_time < (SELECT MAX(scheduler_end_time) FROM dbjudicialdata.dataload_schedular_audit "
            + "WHERE api_name = 'People' AND status IN ('Success')) "
            + "AND api_name = 'People' AND status IN ('Success')", nativeQuery = true)
    LocalDateTime findByScheduleEndTime();


    @Query(value = "SELECT MAX(scheduler_end_time) FROM dbjudicialdata.dataload_schedular_audit "
        + "WHERE scheduler_name = 'judicial-ref-data-elinks' AND api_name = 'PublishSidamIds' "
        + "AND status = 'SUCCESS'", nativeQuery = true)
    LocalDateTime findLatestSuccessfulSchedularEndTime();

}
