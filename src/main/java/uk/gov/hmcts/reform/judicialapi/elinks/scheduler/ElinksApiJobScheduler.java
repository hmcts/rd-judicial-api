package uk.gov.hmcts.reform.judicialapi.elinks.scheduler;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkDeletedWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.SchedulerJobStatusResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.impl.ELinksServiceImpl;
import uk.gov.hmcts.reform.judicialapi.elinks.util.DataloadSchedulerJobAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataExceptionHelper;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataIngestionSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.JobStatus.FAILED;
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


    @Value("${elinks.scheduler.wrapperApiUrl}")
    private String eLinksWrapperBaseUrl;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private DataloadSchedulerJobAudit dataloadSchedulerJobAudit;

    @Autowired
    private RestTemplate restTemplate;

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

    public static final String ELINKS_CONTROLLER_BASE_URL = "/refdata/internal/elink";

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
            log.info("ElinksApiJobScheduler.loadElinksData{} Job execution Start " + eLinksWrapperBaseUrl);

            DataloadSchedulerJob audit = new DataloadSchedulerJob();
            audit.setJobStartTime(jobStartTime);
            audit.setPublishingStatus(RefDataElinksConstants.JobStatus.IN_PROGRESS.getStatus());

            dataloadSchedulerJobAudit.auditSchedulerJobStatus(audit);

            log.info("ElinksApiJobScheduler.loadElinksData Job execution in progress");
            loadElinksData();
            log.info("ElinksApiJobScheduler: loadElinksData Job status - Job execution completed successfully");
        }

    }

    public void loadElinksData() {
        LocalDateTime schedulerStartTime = now();

        try{
            ResponseEntity<ElinkLocationWrapperResponse> locationResponse
                = retrieveLocationDetails();
        } catch(Exception ex) {
            log.error("ElinksApiJobScheduler.loadElinksData Job execution completed failure for Location Response",ex);
            if (ex instanceof HttpClientErrorException)
            {
                HttpClientErrorException exception=(HttpClientErrorException)ex;
                if (exception.getRawStatusCode()==403 && exception.getMessage()
                    .contains("jrd-elinks-location".concat(SPACE).concat(FORBIDDEN_EXCEPTION_LD)))
                {

                    elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                        now(),
                        now(),RefDataElinksConstants.JobStatus.FAILED.getStatus(),LOCATIONAPI);
                }
            }
        }
        try{
        ResponseEntity<ElinkPeopleWrapperResponse> peopleResponse
                = retrievePeopleDetails();
        } catch(Exception ex) {
            log.error("Elinks people retrieval Job execution completed failure for people Response",ex);
            if (ex instanceof HttpClientErrorException)
            {
                HttpClientErrorException exception=(HttpClientErrorException)ex;
                if (exception.getRawStatusCode()==403 && exception.getMessage()
                    .contains("jrd-elinks-load-people".concat(SPACE).concat(FORBIDDEN_EXCEPTION_LD)))
                {

                    elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                        now(),
                        now(),RefDataElinksConstants.JobStatus.FAILED.getStatus(),PEOPLEAPI);
                }
            }
        }
        try{
        ResponseEntity<ElinkLeaversWrapperResponse> leaversResponse
                = retrieveLeaversDetails();
        } catch(Exception ex) {
            log.error("Elinks leavers retrieval Job execution completed failure for leaver Response",ex);
            if (ex instanceof HttpClientErrorException)
            {
                HttpClientErrorException exception=(HttpClientErrorException)ex;
                if (exception.getRawStatusCode()==403 && exception.getMessage()
                    .contains("jrd-elinks-leavers".concat(SPACE).concat(FORBIDDEN_EXCEPTION_LD)))
                {

                    elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                        now(),
                        now(),RefDataElinksConstants.JobStatus.FAILED.getStatus(),LEAVERSAPI);
                }
            }
        }
        try{
            ResponseEntity<ElinkDeletedWrapperResponse> deletedResponse
                = retrieveDeletedDetails();
        } catch(Exception ex) {
            log.error("Elinks deleted retrieval Job execution completed failure for deleted Response",ex);
            if (ex instanceof HttpClientErrorException)
            {
                HttpClientErrorException exception=(HttpClientErrorException)ex;
                if (exception.getRawStatusCode()==403 && exception.getMessage()
                    .contains("jrd-elinks-load-deleted".concat(SPACE).concat(FORBIDDEN_EXCEPTION_LD)))
                {

                    elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                        now(),
                        now(),RefDataElinksConstants.JobStatus.FAILED.getStatus(),DELETEDAPI);
                }
            }
        }
        try{
        ResponseEntity<Object> idamSearchResponse
                = retrieveIdamElasticSearchDetails();
        } catch(Exception ex) {
            log.error("Elinks idam elastic search Job execution completed failure for elastic Response",ex);
            if (ex instanceof HttpClientErrorException)
            {
                HttpClientErrorException exception=(HttpClientErrorException)ex;
                if (exception.getRawStatusCode()==403 && exception.getMessage()
                .contains("jrd-elinks-idam-elastic-search".concat(SPACE).concat(FORBIDDEN_EXCEPTION_LD)))
                {

                    elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                        now(),
                        now(),RefDataElinksConstants.JobStatus.FAILED.getStatus(),ELASTICSEARCH);
                }
            }
        }
        try{
            ResponseEntity<Object> idamResponce
                = retrieveSidamids();
        } catch(Exception ex) {
            log.error("Elinks idam retrieval Job execution completed failure for sidam Response",ex);
            if (ex instanceof HttpClientErrorException)
            {
                HttpClientErrorException exception=(HttpClientErrorException)ex;
                if (exception.getRawStatusCode()==403 && exception.getMessage()
                    .contains("jrd-elinks-idam-sso-search".concat(SPACE).concat(FORBIDDEN_EXCEPTION_LD)))
                {

                    elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                        now(),
                        now(),RefDataElinksConstants.JobStatus.FAILED.getStatus(),IDAMSEARCH);
                }
            }
        }

        try{
        ResponseEntity<SchedulerJobStatusResponse> schedulerResponse
            = retrieveAsbPublishDetails();
        } catch(Exception ex) {
            log.warn("ElinksApiJobScheduler.loadElinksData Job execution completed failure for Publish ASB Response");
            jdbcTemplate.update(UPDATE_JOB_SQL, FAILED.getStatus(),
                dataloadSchedulerJobRepository.findFirstByOrderByIdDesc().getId());

            if (ex instanceof HttpClientErrorException)
            {
                HttpClientErrorException exception=(HttpClientErrorException)ex;
                if (exception.getRawStatusCode()==403 && exception.getMessage()
                .contains("jrd-elinks-publish-service-bus".concat(SPACE).concat(FORBIDDEN_EXCEPTION_LD)))
                {
                    elinkDataIngestionSchedularAudit.auditSchedulerStatus(JUDICIAL_REF_DATA_ELINKS,
                        now(),
                        now(),RefDataElinksConstants.JobStatus.FAILED.getStatus(),PUBLISHSIDAM);
                }
            }
        }
        elinksServiceImpl.cleanUpElinksResponses();
        elinksServiceImpl.deleteJohProfiles(schedulerStartTime);
    }

    public ResponseEntity<ElinkLocationWrapperResponse> retrieveLocationDetails() {


        String apiUrl = eLinksWrapperBaseUrl.concat(ELINKS_CONTROLLER_BASE_URL)
                .concat("/reference_data/location");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        HttpEntity<String> request =
                new HttpEntity<>(headers);

        return restTemplate.exchange(apiUrl,
                HttpMethod.GET, request, ElinkLocationWrapperResponse.class);

    }

    public ResponseEntity<ElinkPeopleWrapperResponse> retrievePeopleDetails() {


        String apiUrl = eLinksWrapperBaseUrl.concat(ELINKS_CONTROLLER_BASE_URL)
                .concat("/people");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        HttpEntity<String> request =
                new HttpEntity<>(headers);

        return restTemplate.exchange(apiUrl,
                HttpMethod.GET, request, ElinkPeopleWrapperResponse.class);

    }

    public ResponseEntity<ElinkLeaversWrapperResponse> retrieveLeaversDetails() {


        String apiUrl = eLinksWrapperBaseUrl.concat(ELINKS_CONTROLLER_BASE_URL)
                .concat("/leavers");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        HttpEntity<String> request =
                new HttpEntity<>(headers);

        return restTemplate.exchange(apiUrl,
                HttpMethod.GET, request, ElinkLeaversWrapperResponse.class);

    }

    public ResponseEntity<ElinkDeletedWrapperResponse> retrieveDeletedDetails() {


        String apiUrl = eLinksWrapperBaseUrl.concat(ELINKS_CONTROLLER_BASE_URL)
            .concat("/deleted");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        HttpEntity<String> request =
            new HttpEntity<>(headers);

        return restTemplate.exchange(apiUrl,
            HttpMethod.GET, request, ElinkDeletedWrapperResponse.class);

    }

    private ResponseEntity<Object> retrieveSidamids() {
        String apiUrl = eLinksWrapperBaseUrl.concat(ELINKS_CONTROLLER_BASE_URL)
            .concat("/idam/find");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        HttpEntity<String> request =
            new HttpEntity<>(headers);

        return restTemplate.exchange(apiUrl,
            HttpMethod.GET, request, Object.class);
    }

    public ResponseEntity<Object> retrieveIdamElasticSearchDetails() {



        String apiUrl = eLinksWrapperBaseUrl.concat(ELINKS_CONTROLLER_BASE_URL)
                .concat("/idam/elastic/search");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        HttpEntity<String> request =
                new HttpEntity<>(headers);

        return restTemplate.exchange(apiUrl,
                HttpMethod.GET, request, Object.class);

    }

    public ResponseEntity<SchedulerJobStatusResponse> retrieveAsbPublishDetails() {

        String apiUrl = eLinksWrapperBaseUrl.concat(ELINKS_CONTROLLER_BASE_URL)
            .concat("/sidam/asb/publish");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        HttpEntity<String> request =
            new HttpEntity<>(headers);

        return restTemplate.exchange(apiUrl,
            HttpMethod.GET, request, SchedulerJobStatusResponse.class);

    }

}
