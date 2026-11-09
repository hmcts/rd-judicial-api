package uk.gov.hmcts.reform.judicialapi.elinks.scheduler;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ForbiddenException;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ELinksService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.IdamElasticSearchService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.impl.ELinksServiceImpl;
import uk.gov.hmcts.reform.judicialapi.elinks.util.DataloadSchedulerJobAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataExceptionHelper;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataIngestionSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.JobStatus.FAILED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.JobStatus.IN_PROGRESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.JobStatus.SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DELETEDAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELASTICSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAMSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PUBLISHSIDAM;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.SqlContants.UPDATE_JOB_SQL;
import static uk.gov.hmcts.reform.judicialapi.util.FeatureConditionEvaluation.FORBIDDEN_EXCEPTION_LD;

@Component
@Slf4j
@NoArgsConstructor
@SuppressWarnings("all")
public class ElinksApiJobScheduler {

    @Autowired
    ELinksService eLinksService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private DataloadSchedulerJobAudit dataloadSchedulerJobAudit;

    @Value("${elinks.scheduler.enabled:false}")
    private boolean isSchedulerEnabled;

    @Autowired
    ElinkDataIngestionSchedularAudit elinkDataIngestionSchedularAudit;


    @Autowired
    ElinkDataExceptionHelper elinkDataExceptionHelper;

    @Autowired
    DataloadSchedulerJobRepository dataloadSchedulerJobRepository;

    @Autowired
    ELinksServiceImpl elinksServiceImpl;

    @Autowired
    ElinksPeopleService elinksPeopleService;

    @Autowired
    IdamElasticSearchService idamElasticSearchService;

    @Autowired
    PublishSidamIdService publishSidamIdService;

    @Value("${elinks.scheduler.steps.loadLocation.enabled:false}")
    private boolean isloadLocationEnabled;

    @Value("${elinks.scheduler.steps.loadPerson.enabled:false}")
    private boolean isloadPersonEnabled;

    @Value("${elinks.scheduler.steps.loadDeleted.enabled:false}")
    private boolean isloadDeletedEnabled;

    @Value("${elinks.scheduler.steps.loadLeavers.enabled:false}")
    private boolean isloadLeaversEnabled;

    @Value("${elinks.scheduler.steps.idamElasticSearch.enabled:false}")
    private boolean isidamElasticSearchEnabled;

    @Value("${elinks.scheduler.steps.publishSidamIdToAsb.enabled:false}")
    private boolean ispublishSidamIdToAsbEnabled;

    @Value("${elinks.scheduler.steps.fetchIdamIds.enabled:false}")
    private boolean isfetchIdamIdsEnabled;

    @Scheduled(cron = "${elinks.scheduler.cronExpression}")
    @SchedulerLock(name = "lockedTask", lockAtMostFor = "${elinks.scheduler.lockAtMostFor}",
            lockAtLeastFor = "${elinks.scheduler.lockAtLeastFor}")
    public void loadElinksJob() {

        if (isSchedulerEnabled) {
            LocalDateTime jobStartTime = now();

            DataloadSchedulerJob latestEntry = dataloadSchedulerJobRepository.findFirstByOrderByIdDesc();

            if(Optional.ofNullable(latestEntry).isPresent()) {

                LocalDate startDate = Optional.ofNullable(latestEntry.getJobStartTime()).isPresent() ? latestEntry
                        .getJobStartTime().toLocalDate() : null;
                LocalDate endDate = Optional.ofNullable(latestEntry.getJobEndTime()).isPresent() ? latestEntry
                        .getJobEndTime().toLocalDate() : null;
                LocalDate currentDate = jobStartTime.toLocalDate();

                if (currentDate.equals(startDate) || currentDate.equals(endDate)) {
                    log.info("JRD load failed since job has already ran for the day");
                    elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                            jobStartTime,
                            "ElinksApiJobScheduler" + jobStartTime,
                            "Schedular_Run_date", "JRD load failed since job has already ran for the day",
                        "ElinksApiJobScheduler",null,1);
                    return;
                }
            }
            log.info("ElinksApiJobScheduler.loadElinksData{} Job execution Start");

            DataloadSchedulerJob audit = new DataloadSchedulerJob();
            audit.setJobStartTime(jobStartTime);
            audit.setPublishingStatus(RefDataElinksConstants.JobStatus.IN_PROGRESS.getStatus());

            audit = dataloadSchedulerJobAudit.auditSchedulerJobStatus(audit);

            log.info("ElinksApiJobScheduler.loadElinksData Job execution in progress");
            loadElinksData();
            updateSchedulerJobStatus(audit.getId(), SUCCESS.getStatus());
            log.info("ElinksApiJobScheduler: loadElinksData Job status - Job execution completed successfully");
        }

    }

    public void loadElinksData() {
        LocalDateTime schedulerStartTime = now();
        if (isloadLocationEnabled) {
            try {
                eLinksService.retrieveLocation();
            } catch (Exception ex) {
                handleStepFailure(ex,
                    "ElinksApiJobScheduler.loadElinksData Job execution completed failure for Location Response",
                    "jrd-elinks-location",
                    LOCATIONAPI);
            }
        }

        if (isloadPersonEnabled) {
            try {
                elinksPeopleService.updatePeople();
            } catch (Exception ex) {
                handleStepFailure(ex,
                    "Elinks people retrieval Job execution completed failure for people Response",
                    "jrd-elinks-load-people",
                    PEOPLEAPI);
            }
        }

        if (isloadLeaversEnabled) {
            try {
                eLinksService.retrieveLeavers();
            } catch (Exception ex) {
                handleStepFailure(ex,
                    "Elinks leavers retrieval Job execution completed failure for leaver Response",
                    "jrd-elinks-leavers",
                    LEAVERSAPI);
            }
        }

        if (isloadDeletedEnabled) {
            try {
                eLinksService.retrieveDeleted();
            } catch (Exception ex) {
                handleStepFailure(ex,
                    "Elinks deleted retrieval Job execution completed failure for deleted Response",
                    "jrd-elinks-load-deleted",
                    DELETEDAPI);
            }
        }

        if (isidamElasticSearchEnabled) {
            try {
                idamElasticSearchService.getIdamElasticSearchSyncFeed();
            } catch (Exception ex) {
                handleStepFailure(ex,
                    "Elinks idam elastic search Job execution completed failure for elastic Response",
                    "jrd-elinks-idam-elastic-search",
                    ELASTICSEARCH);
            }
        }

        if (isfetchIdamIdsEnabled) {
            try {
                idamElasticSearchService.getIdamDetails();
            } catch (Exception ex) {
                handleStepFailure(ex,
                    "Elinks idam retrieval Job execution completed failure for sidam Response",
                    "jrd-elinks-idam-sso-search",
                    IDAMSEARCH);
            }
        }

        if (ispublishSidamIdToAsbEnabled) {
            try {
                publishSidamIdService.publishSidamIdToAsb();
            } catch (Exception ex) {
                log.warn(
                    "ElinksApiJobScheduler.loadElinksData Job execution completed failure for Publish ASB Response");
                jdbcTemplate.update(UPDATE_JOB_SQL, FAILED.getStatus(),
                    dataloadSchedulerJobRepository.findFirstByOrderByIdDesc().getId());
                auditFeatureFlagFailure(ex, "jrd-elinks-publish-service-bus", PUBLISHSIDAM);
            }
        }

        elinksServiceImpl.cleanUpElinksResponses();
        elinksServiceImpl.deleteJohProfiles(schedulerStartTime);
    }

    private void handleStepFailure(Exception ex, String logMessage, String flagName, String apiName) {
        log.error(logMessage, ex);
        auditFeatureFlagFailure(ex, flagName, apiName);
    }

    private void auditFeatureFlagFailure(Exception ex, String flagName, String apiName) {
        if (isFeatureFlagForbidden(ex, flagName)) {
            elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS, now(), now(),
                RefDataElinksConstants.JobStatus.FAILED.getStatus(), apiName, ex.getMessage());
        }
    }

    private boolean isFeatureFlagForbidden(Exception ex, String flagName) {
        String expectedMessage = flagName.concat(SPACE).concat(FORBIDDEN_EXCEPTION_LD);
        if (ex instanceof HttpClientErrorException exception) {
            return exception.getStatusCode() == HttpStatus.FORBIDDEN
                && nonNull(exception.getMessage())
                && exception.getMessage().contains(expectedMessage);
        }
        if (ex instanceof ElinksException exception) {
            return exception.getStatus() == HttpStatus.FORBIDDEN
                && nonNull(exception.getMessage())
                && exception.getMessage().contains(expectedMessage);
        }
        return ex instanceof ForbiddenException
            && nonNull(ex.getMessage())
            && ex.getMessage().contains(expectedMessage);
    }

    private void updateSchedulerJobStatus(Integer jobId, String targetStatus) {
        if (jobId == null) {
            return;
        }

        DataloadSchedulerJob currentJob = dataloadSchedulerJobRepository.findFirstByOrderByIdDesc();
        if (currentJob != null && IN_PROGRESS.getStatus().equals(currentJob.getPublishingStatus())) {
            jdbcTemplate.update(UPDATE_JOB_SQL, targetStatus, jobId);
        }
    }

}
