package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.ElinkEmailConfiguration;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.JudicialDataLoadException;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.SchedulerJobStatusResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.IEmailService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.dto.Email;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;
import uk.gov.hmcts.reform.judicialapi.elinks.util.CommonUtil;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataIngestionSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.util.CollectionUtils.isEmpty;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.JobStatus.FAILED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.JobStatus.IN_PROGRESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.JobStatus.SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataConstants.CONTENT_TYPE_PLAIN;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.AUDIT_DATA_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DATABASE_FETCH_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JOB_DETAILS_UPDATE_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PUBLISHSIDAM;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.SqlContants.GET_DISTINCT_SIDAM_ID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.SqlContants.SELECT_JOB_STATUS_SQL;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.SqlContants.UPDATE_JOB_SQL;


@Slf4j
@Component
public class PublishSidamIdServiceImpl implements PublishSidamIdService {

    private static final String ZERO = "0";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("${logging-component-name}")
    String logComponentName;

    @Autowired
    ElinkTopicPublisher elinkTopicPublisher;

    @Autowired
    IEmailService emailService;

    @Autowired
    ElinkEmailConfiguration emailConfiguration;

    @Autowired
    ElinkDataIngestionSchedularAudit elinkDataIngestionSchedularAudit;

    @Autowired
    private DataloadSchedularAuditRepository dataloadSchedularAuditRepository;

    @Autowired
    private ProfileRepository userProfileRepository;

    @Autowired
    CommonUtil commonUtil;

    @Value("${launchdarkly.sdk.environment}")
    String environment;

    @Value("${jrd.publisher.publish-Idams-delta}")
    boolean publishIdamsDelta;

    @Value("${jrd.publisher.no_of_hours}")
    int noOfHours;

    private int sidamIdcount;

    public ResponseEntity<SchedulerJobStatusResponse> publishSidamIdToAsb() throws JudicialDataLoadException {
        List<String> sidamIds;
        String lastSuccessSince;
        LocalDateTime maxSuccessfulSchedulerEndTime;
        if (publishIdamsDelta) {
            log.info("{}:: Publish Sidam Id delta load to ASB is enabled as publish-Idams-delta is set to {true}",
                logComponentName, publishIdamsDelta);
            // Get delta load of sidam id's from the judicial_user_profile table
            try {
                maxSuccessfulSchedulerEndTime = dataloadSchedularAuditRepository.findLatestSuccessfulSchedularEndTime();
            } catch (Exception ex) {
                throw new ElinksException(HttpStatus.NOT_ACCEPTABLE, AUDIT_DATA_ERROR, AUDIT_DATA_ERROR);
            }
            //if maxSuccessfulSchedulerEndTime is null, then set it to an old date in the past to get all records
            if (Optional.ofNullable(maxSuccessfulSchedulerEndTime).isEmpty()) {
                maxSuccessfulSchedulerEndTime = LocalDateTime.now().minusYears(8);
            }
            LocalDateTime newTime = maxSuccessfulSchedulerEndTime.minusHours(noOfHours);
            sidamIds = userProfileRepository
                .fetchDeltaLoadIdamIds(maxSuccessfulSchedulerEndTime,newTime);
        } else {
            // Get all sidam id's from the judicial_user_profile table
            sidamIds = jdbcTemplate.query(GET_DISTINCT_SIDAM_ID, RefDataConstants.ROW_MAPPER);
        }
        return publishSidamIdToAsb(sidamIds);
    }

    @Override
    public ResponseEntity<SchedulerJobStatusResponse> publishSidamIdToAsb(List<String> sidamIds)
            throws JudicialDataLoadException {
        //Get the job details from dataload_schedular_job table

        log.info("Calling Publish Sidam id Service");

        Pair<String, String> jobDetails;
        LocalDateTime schedulerStartTime = now();
        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                null,
                RefDataElinksConstants.JobStatus.IN_PROGRESS.getStatus(), PUBLISHSIDAM);
        try {
            jobDetails = getJobDetails(SELECT_JOB_STATUS_SQL);
        } catch (Exception ex) {
            throw new ElinksException(HttpStatus.BAD_REQUEST, DATABASE_FETCH_ERROR, ex.getMessage());
        }

        sidamIds =
                sidamIds.stream()
                        .map(String::strip)
                        .filter(s -> !s.isBlank())
                        .toList();

        sidamIdcount = sidamIds.size();

        log.info("{}::Total SIDAM Id count from JUD_Database: {}", logComponentName, sidamIdcount);
        if (isEmpty(sidamIds)) {
            log.warn("{}:: No Sidam id exists in JRD for publishing in ASB for JOB id: {} ",
                    logComponentName, jobDetails.getLeft());
            updateAsbStatus(jobDetails.getLeft(), SUCCESS.getStatus(),schedulerStartTime);
        }

        publishMessage(jobDetails.getRight(), sidamIds, jobDetails.getLeft(),schedulerStartTime);
        jobDetails = getJobDetails(SELECT_JOB_STATUS_SQL);
        log.info("{}:: completed Publish SidamId to ASB with JOB Id: {}  ",
                logComponentName, jobDetails.getLeft());

        SchedulerJobStatusResponse response = SchedulerJobStatusResponse.builder().id(jobDetails.getLeft())
                .jobStatus(jobDetails.getRight()).sidamIdsCount(sidamIdcount).statusCode(HttpStatus.OK.value()).build();

        elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                now(),
                RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), PUBLISHSIDAM);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private Pair<String, String> getJobDetails(String jobStatusQuery) {
        Optional<Pair<String, String>> pair = getElinkJobStatus(jobStatusQuery);

        final String jobId = pair.map(Pair::getLeft).orElse(ZERO);
        final String jobStatus = pair.map(Pair::getRight).orElse(EMPTY);
        return Pair.of(jobId, jobStatus);
    }

    private Optional<Pair<String, String>> getElinkJobStatus(String jobStatusQuery) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(jobStatusQuery, (resultSet, i) ->
                Pair.of(resultSet.getString(1), resultSet.getString(2))));
        } catch (EmptyResultDataAccessException ex) {
            log.info("No record found in table dataload_schedular_job");
            return Optional.empty();
        }
    }


    public void publishMessage(String status, List<String> sidamIds, String jobId,
                               LocalDateTime schedulerStartTime) {
        try {
            if ((IN_PROGRESS.getStatus().equals(status)) && isNotEmpty(sidamIds)) {
                //Publish or retry Message in ASB
                log.info("{}:: Publishing/Retrying JRD messages in ASB for Job Id {}", logComponentName, jobId);
                elinkTopicPublisher.sendMessage(sidamIds, jobId);
                updateAsbStatus(jobId, SUCCESS.getStatus(),schedulerStartTime);
                log.info("{}:: Updated Total distinct Sidam Ids to ASB: {}", logComponentName, sidamIdcount);
            }
        } catch (Exception ex) {
            log.error("ASB Failure Root cause - {}", ex.getMessage());
            ElinkEmailConfiguration.MailTypeConfig mailTypeConfig = emailConfiguration.getMailTypes().get("asb");
            final String logMessage = String.format(mailTypeConfig.getSubject(), jobId);
            log.error("{}:: {}", logComponentName, logMessage);
            updateAsbStatus(jobId, FAILED.getStatus(),schedulerStartTime);
            if (mailTypeConfig.isEnabled()) {
                Email email = Email.builder()
                    .contentType(CONTENT_TYPE_PLAIN)
                    .from(mailTypeConfig.getFrom())
                    .to(mailTypeConfig.getTo())
                    .messageBody(String.format(mailTypeConfig.getBody(), jobId))
                    .subject(String.format(mailTypeConfig.getSubject(), environment))
                    .build();
                emailService.sendEmail(email);
            }
            elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                now(),
                RefDataElinksConstants.JobStatus.FAILED.getStatus(), PUBLISHSIDAM, ex.getMessage());
            throw ex;
        }
    }

    private void updateAsbStatus(String jobId,String jobStatus,LocalDateTime schedulerStartTime) {
        //Update elinks DB with Publishing Status
        try {
            jdbcTemplate.update(UPDATE_JOB_SQL, jobStatus, Integer.valueOf(jobId));
        } catch (Exception ex) {
            elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                schedulerStartTime,
                now(),
                RefDataElinksConstants.JobStatus.FAILED.getStatus(), PUBLISHSIDAM, ex.getMessage());
            throw new ElinksException(HttpStatus.FORBIDDEN, JOB_DETAILS_UPDATE_ERROR, JOB_DETAILS_UPDATE_ERROR);
        }

    }
}
