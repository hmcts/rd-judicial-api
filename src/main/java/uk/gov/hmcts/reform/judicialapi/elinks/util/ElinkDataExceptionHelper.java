package uk.gov.hmcts.reform.judicialapi.elinks.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataExceptionRecords;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;

import java.time.LocalDateTime;

@Component
@Slf4j
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ElinkDataExceptionHelper {

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    @Autowired
    ElinkDataExceptionRepository elinkDataExceptionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditException(String schedulerName, LocalDateTime schedulerStartTime,
                               String key, String fieldInError, String errorDescription,
                               String tableName,String personalCode,int pageValue) {

        ElinkDataExceptionRecords audit = new ElinkDataExceptionRecords();
        try {

            audit.setSchedulerName(schedulerName);
            audit.setSchedulerStartTime(schedulerStartTime);
            audit.setKey(key);
            audit.setFieldInError(fieldInError);
            audit.setErrorDescription(errorDescription);
            audit.setTableName(tableName);
            audit.setUpdatedTimeStamp(LocalDateTime.now());
            audit.setRowId(personalCode);
            audit.setPageId(pageValue);
            elinkDataExceptionRepository.save(audit);
        } catch (Exception e) {
            log.error("{}:: Failure error Message {} in auditSchedulerStatus {}  ",
                    loggingComponentName, e.getMessage(), schedulerName);
            throw e;
        }
    }

}