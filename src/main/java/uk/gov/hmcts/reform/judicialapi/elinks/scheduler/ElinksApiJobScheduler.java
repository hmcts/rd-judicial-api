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

            try {
                log.info("ElinksApiJobScheduler.loadElinksData Job execution in progress");

                loadElinksData();

                LocalDateTime jobEndTime = now();
                audit.setJobEndTime(jobEndTime);
                audit.setPublishingStatus(RefDataElinksConstants.JobStatus.SUCCESS.getStatus());

                dataloadSchedulerJobAudit.auditSchedulerJobStatus(audit);

            } catch (Exception exception) {
                log.info("ElinksApiJobScheduler.loadElinksData Job execution completed failure");

                LocalDateTime jobEndTime = now();
                audit.setJobEndTime(jobEndTime);
                audit.setPublishingStatus(RefDataElinksConstants.JobStatus.FAILED.getStatus());
                dataloadSchedulerJobAudit.auditSchedulerJobStatus(audit);
            }
            log.info("ElinksApiJobScheduler.loadElinksData Job execution completed successful");
        }

    }

    public void loadElinksData() {

        ResponseEntity<ElinkLocationWrapperResponse> locationResponse
                = retrieveLocationDetails();
        ResponseEntity<ElinkBaseLocationWrapperResponse> baseLocationResponse
                = retrieveBaseLocationDetails();

        ResponseEntity<ElinkPeopleWrapperResponse> peopleResponse
                = retrievePeopleDetails();

        ResponseEntity<ElinkLeaversWrapperResponse> leaversResponse
                = retrieveLeaversDetails();
        ResponseEntity<Object> idamSearchResponse
                = retrieveIdamElasticSearchDetails();
        //Publish Api is pending
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

}
