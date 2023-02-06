package uk.gov.hmcts.reform.judicialapi.elinks.scheduler;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.SchedulerJobStatusResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.DataloadSchedulerJobAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@Slf4j
@NoArgsConstructor
@SuppressWarnings("all")
public class ElinksApiJobScheduler {


    @Value("${elinks.scheduler.wrapperApiUrl}")
    private String eLinksWrapperBaseUrl;

    @Autowired
    private DataloadSchedulerJobAudit dataloadSchedulerJobAudit;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${elinks.scheduler.enabled:false}")
    private boolean isSchedulerEnabled;


    public static final String ELINKS_CONTROLLER_BASE_URL =
            "/refdata/internal/elink";

    @Scheduled(cron = "${elinks.scheduler.cronExpression}")
    public void loadElinksJob() {

        if (isSchedulerEnabled) {

            log.info("ElinksApiJobScheduler.loadElinksData{} Job execution Start " + eLinksWrapperBaseUrl);

            DataloadSchedulerJob audit = new DataloadSchedulerJob();
            LocalDateTime jobStartTime = now();

            audit.setJobStartTime(jobStartTime);
            audit.setPublishingStatus(RefDataElinksConstants.JobStatus.IN_PROGRESS.getStatus());

            dataloadSchedulerJobAudit.auditSchedulerJobStatus(audit);


            log.info("ElinksApiJobScheduler.loadElinksData Job execution in progress");
            loadElinksData();
            log.info("ElinksApiJobScheduler.loadElinksData Job execution completed successful");
        }

    }

    public void loadElinksData() {

        try{
            ResponseEntity<ElinkLocationWrapperResponse> locationResponse
                = retrieveLocationDetails();
        } catch(Exception ex) {
            log.info("ElinksApiJobScheduler.loadElinksData Job execution completed failure for Location");
        }
        try{
        ResponseEntity<ElinkBaseLocationWrapperResponse> baseLocationResponse
                = retrieveBaseLocationDetails();
        } catch(Exception ex) {
            log.info("ElinksApiJobScheduler.loadElinksData Job execution completed failure for Base Location");
        }
        try{
        ResponseEntity<ElinkPeopleWrapperResponse> peopleResponse
                = retrievePeopleDetails();
        } catch(Exception ex) {
            log.info("ElinksApiJobScheduler.loadElinksData Job execution completed failure for People Response");
        }
        try{
        ResponseEntity<ElinkLeaversWrapperResponse> leaversResponse
                = retrieveLeaversDetails();
        } catch(Exception ex) {
            log.info("ElinksApiJobScheduler.loadElinksData Job execution completed failure for Leavers Response");
        }
        try{
        ResponseEntity<Object> idamSearchResponse
                = retrieveIdamElasticSearchDetails();
        } catch(Exception ex) {
            log.info("ElinksApiJobScheduler.loadElinksData Job execution completed failure for idamSearch Response");
        }
        try{
        ResponseEntity<SchedulerJobStatusResponse> schedulerResponse
            = retrieveAsbPublishDetails();
        } catch(Exception ex) {
            log.info("ElinksApiJobScheduler.loadElinksData Job execution completed failure for Publish ASB Response");
        }
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

    public ResponseEntity<ElinkBaseLocationWrapperResponse> retrieveBaseLocationDetails() {


        String apiUrl = eLinksWrapperBaseUrl.concat(ELINKS_CONTROLLER_BASE_URL)
                .concat("/reference_data/base_location");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        HttpEntity<String> request =
                new HttpEntity<>(headers);

        return restTemplate.exchange(apiUrl,
                HttpMethod.GET, request, ElinkBaseLocationWrapperResponse.class);

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
