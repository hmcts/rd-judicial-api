package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;

public interface DataloadSchedulerJobRepository extends JpaRepository<DataloadSchedulerJob, Long> {

    DataloadSchedulerJob findFirstByOrderByIdDesc();

}
