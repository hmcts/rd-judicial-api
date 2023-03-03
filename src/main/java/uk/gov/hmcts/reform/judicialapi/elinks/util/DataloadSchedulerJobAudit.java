package uk.gov.hmcts.reform.judicialapi.elinks.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;

import java.time.LocalDateTime;

@Component
@Slf4j
//@Transactional(propagation = Propagation.REQUIRES_NEW)
public class DataloadSchedulerJobAudit {

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    @Autowired
    private DataloadSchedulerJobRepository dataloadSchedulerJobRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DataloadSchedulerJob auditSchedulerJobStatus(LocalDateTime jobStartTime,
                                     LocalDateTime jobEndTime, String pubStatus) {

        DataloadSchedulerJob audit = new DataloadSchedulerJob();
        try {
            audit.setJobStartTime(jobStartTime);
            audit.setJobEndTime(jobEndTime);
            audit.setPublishingStatus(pubStatus);


            dataloadSchedulerJobRepository.save(audit);
        } catch (Exception e) {
            log.error("{}:: Failure error Message {} in auditSchedulerStatus  ",
                    loggingComponentName, e.getMessage());
        }

        return audit;
    }


    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    public DataloadSchedulerJob auditSchedulerJobStatus(DataloadSchedulerJob audit) {

        try {
            dataloadSchedulerJobRepository.save(audit);
        } catch (Exception e) {
            log.error("{}:: Failure error Message {} in auditSchedulerStatus  ",
                    loggingComponentName, e.getMessage());
        }

        return audit;
    }

}
