package uk.gov.hmcts.reform.judicialapi.elinks.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.util.DataloadSchedulerJobAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ElinksApiJobSchedulerTest {


    @Mock
    private DataloadSchedulerJobAudit dataloadSchedulerJobAudit;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    @Spy
    private ElinksApiJobScheduler elinksApiJobScheduler;



    @Test
    void test_load_elinks_job() {

        ReflectionTestUtils.setField(elinksApiJobScheduler, "eLinksWrapperBaseUrl",
                "http://localhost:8093");


        DataloadSchedulerJob audit = new DataloadSchedulerJob();
        LocalDateTime jobStartTime = now();

        audit.setJobStartTime(jobStartTime);

        LocalDateTime jobEndTime = now();
        audit.setJobEndTime(jobEndTime);
        audit.setPublishingStatus(RefDataElinksConstants.JobStatus.SUCCESS.getStatus());


        elinksApiJobScheduler.loadElinksJob();

        assertThat(elinksApiJobScheduler).isNotNull();

        verify(elinksApiJobScheduler, times(1)).loadElinksJob();
        verify(elinksApiJobScheduler, times(0)).retrieveLocationDetails();
        verify(elinksApiJobScheduler, times(0)). retrievePeopleDetails();
        verify(elinksApiJobScheduler, times(0)). retrieveLeaversDetails();
        verify(elinksApiJobScheduler, times(0)). retrieveDeletedDetails();
        verify(elinksApiJobScheduler, times(0)).  retrieveIdamElasticSearchDetails();

    }
}
