package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataloadSchedulerJobRepositoryTest {

    @Spy
    private DataloadSchedulerJobRepository dataloadSchedulerJobRepository;



    @Test
    void test_save_job_status() {

        DataloadSchedulerJob audit = new DataloadSchedulerJob();
        LocalDateTime jobStartTime = now();

        audit.setJobStartTime(jobStartTime);

        LocalDateTime jobEndTime = now();
        audit.setJobEndTime(jobEndTime);
        audit.setPublishingStatus(RefDataElinksConstants.JobStatus.SUCCESS.getStatus());



        when(dataloadSchedulerJobRepository.save(any())).thenReturn(audit);

        DataloadSchedulerJob result = dataloadSchedulerJobRepository.save(audit);

        assertThat(result.getId()).isEqualTo(audit.getId());
        assertThat(result.getJobStartTime()).isEqualTo(audit.getJobStartTime());
        assertThat(result.getJobEndTime()).isEqualTo(audit.getJobEndTime());
        assertThat(result.getPublishingStatus()).isEqualTo(audit.getPublishingStatus());
    }
}
