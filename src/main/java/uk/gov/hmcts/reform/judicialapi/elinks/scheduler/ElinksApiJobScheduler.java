package uk.gov.hmcts.reform.judicialapi.elinks.scheduler;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.util.DataloadSchedulerJobAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.time.LocalDateTime;
import javax.servlet.ServletContext;

import static java.time.LocalDateTime.now;

@Service
@Slf4j
@NoArgsConstructor
//@AllArgsConstructor
public class ElinksApiJobScheduler {

    @Autowired
    private ServletContext context;

    @Autowired
    private DataloadSchedulerJobAudit dataloadSchedulerJobAudit;

    //@Scheduled(cron = "${scheduler.config}")
    //Every 5 seconds
    @Scheduled(cron = "*/5 * * * * *")
    public void loadElinksJob() {

        //String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        //String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().;
        String baseUrl = context.getContextPath();
        log.info("ElinksApiJobScheduler.loadElinksData{} Job execution Start" + baseUrl);
        DataloadSchedulerJob audit = new DataloadSchedulerJob();
        LocalDateTime jobStartTime = now();

        audit.setJobStartTime(jobStartTime);
        audit.setPublishingStatus(RefDataElinksConstants.JobStatus.IN_PROGRESS.getStatus());

        dataloadSchedulerJobAudit.auditSchedulerJobStatus(audit);

        try {
            log.info("ElinksApiJobScheduler.loadElinksData{} Job execution in progress" + baseUrl);

            loadElinksData();

            LocalDateTime jobEndTime = now();
            audit.setJobEndTime(jobEndTime);
            audit.setPublishingStatus(RefDataElinksConstants.JobStatus.SUCCESS.getStatus());

            dataloadSchedulerJobAudit.auditSchedulerJobStatus(audit);

        } catch (Exception exception) {
            log.info("ElinksApiJobScheduler.loadElinksData{} Job execution completed failure" + baseUrl);

            LocalDateTime jobEndTime = now();
            audit.setJobEndTime(jobEndTime);
            audit.setPublishingStatus(RefDataElinksConstants.JobStatus.FAILED.getStatus());
            dataloadSchedulerJobAudit.auditSchedulerJobStatus(audit);
        }
        log.info("ElinksApiJobScheduler.loadElinksData{} Job execution completed successful" + baseUrl);
    }

    public void loadElinksData(){

        //throw new RuntimeException();
    }
}
