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
import java.util.List;

import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DATE_OF_DELETION;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.USER_PROFILE;

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
                               String tableName, String personalCode, Integer pageValue) {
        auditExceptionMessage(schedulerName,
                schedulerStartTime,
                key,
                fieldInError,
                errorDescription,
                tableName,
                personalCode,
                pageValue,
                "");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditException(String schedulerName, LocalDateTime schedulerStartTime,
                               String key, String fieldInError, String errorDescription,
                               String tableName, String personalCode,
                               Integer pageValue, String errorMessage) {

        auditExceptionMessage(schedulerName,
                schedulerStartTime,
                key,
                fieldInError,
                errorDescription,
                tableName,
                personalCode,
                pageValue,
                errorMessage);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditException(List<String> personalCodes, LocalDateTime schedulerStartTime) {
        try {
            List<ElinkDataExceptionRecords> records =
                    personalCodes.stream().map(p -> auditEntity(p, schedulerStartTime)).toList();
            elinkDataExceptionRepository.saveAll(records);
        } catch (Exception e) {
            log.error("{}:: Failure error Message {} in auditSchedulerStatus {}  ",
                    loggingComponentName, e.getMessage(), JUDICIAL_REF_DATA_ELINKS);
            throw e;
        }
    }

    private void auditExceptionMessage(String schedulerName,
                                       LocalDateTime schedulerStartTime,
                                       String key,
                                       String fieldInError,
                                       String errorDescription,
                                       String tableName,
                                       String personalCode,
                                       Integer pageValue,
                                       String errorMessage) {
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
            audit.setErrorMessage(errorMessage != null && errorMessage.length() > 500
                    ? errorMessage.substring(0, 500) : errorMessage);
            elinkDataExceptionRepository.save(audit);
        } catch (Exception e) {
            log.error("{}:: Failure error Message {} in auditSchedulerStatus {}  ",
                    loggingComponentName, e.getMessage(), schedulerName);
            throw e;
        }
    }

    private ElinkDataExceptionRecords auditEntity(String perCode, LocalDateTime startTime) {
        ElinkDataExceptionRecords audit = new ElinkDataExceptionRecords();
        audit.setSchedulerName(JUDICIAL_REF_DATA_ELINKS);
        audit.setSchedulerStartTime(startTime);
        audit.setKey(perCode);
        audit.setFieldInError(DATE_OF_DELETION);
        audit.setErrorDescription("JOH profile is removed as it was deleted 7 years ago in Judicial Office");
        audit.setTableName(USER_PROFILE);
        audit.setUpdatedTimeStamp(LocalDateTime.now());
        audit.setRowId(perCode);
        return audit;
    }
}