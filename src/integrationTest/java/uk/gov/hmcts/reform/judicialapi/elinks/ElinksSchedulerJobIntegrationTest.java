package uk.gov.hmcts.reform.judicialapi.elinks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.scheduler.ElinksApiJobScheduler;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.util.HashSet;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSSUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;

class ElinksSchedulerJobIntegrationTest extends ElinksEnabledIntegrationTest {


    @Autowired
    LocationRepository locationRepository;
    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    AppointmentsRepository appointmentsRepository;

    @Autowired
    AuthorisationsRepository authorisationsRepository;
    @Autowired
    BaseLocationRepository baseLocationRepository;

    @Autowired
    private ElinkSchedularAuditRepository elinkSchedularAuditRepository;

    @Autowired
    private ElinksApiJobScheduler elinksApiJobScheduler;

    @Autowired
    private DataloadSchedulerJobRepository dataloadSchedulerJobRepository;

    @Autowired
    IdamTokenConfigProperties tokenConfigProperties;





    @DisplayName("Elinks load eLinks scheduler status verification failure case")
    @Test
    void test_load_elinks_job_status_failure() {


        int statusCode = 400;
        locationApi4xxResponse(statusCode,null);

        elinksApiJobScheduler.loadElinksJob();

        DataloadSchedulerJob jobDetails = dataloadSchedulerJobRepository.findAll().get(0);

        assertThat(jobDetails).isNotNull();
        assertThat(jobDetails.getPublishingStatus()).isEqualTo(RefDataElinksConstants.JobStatus.FAILED.getStatus());

    }



    private void setupData() throws JOSEException, JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        ElinkLocationWrapperResponse elinkLocationWrapperResponse = new ElinkLocationWrapperResponse();
        elinkLocationWrapperResponse.setMessage(LOCATION_DATA_LOAD_SUCCESS);

        elinks.stubFor(get(urlPathMatching("/location"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(objectMapper.writeValueAsString(elinkLocationWrapperResponse))
                ));

        ElinkBaseLocationWrapperResponse elinkBaseLocationWrapperResponse = new ElinkBaseLocationWrapperResponse();
        elinkLocationWrapperResponse.setMessage(BASE_LOCATION_DATA_LOAD_SUCCESS);

        elinks.stubFor(get(urlPathMatching("/base_location"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(objectMapper.writeValueAsString(elinkBaseLocationWrapperResponse))
                ));


        ElinkPeopleWrapperResponse elinkPeopleWrapperResponse = new ElinkPeopleWrapperResponse();
        elinkPeopleWrapperResponse.setMessage(PEOPLE_DATA_LOAD_SUCCESS);


        elinks.stubFor(get(urlPathMatching("/people"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(objectMapper.writeValueAsString(elinkPeopleWrapperResponse))
                ));


        Set<IdamResponse> idamResponseSet = new HashSet<>();
        idamResponseSet.add(new IdamResponse());

        elinks.stubFor(get(urlPathMatching("/idam/elastic/search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(objectMapper.writeValueAsString(idamResponseSet))
                ));



        ElinkLeaversWrapperResponse elinkLeaversWrapperResponse = new ElinkLeaversWrapperResponse();
        elinkLeaversWrapperResponse.setMessage(LEAVERSSUCCESS);

        elinks.stubFor(get(urlPathMatching("/leavers"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(objectMapper.writeValueAsString(elinkLeaversWrapperResponse))
                ));


    }

    private void locationApi4xxResponse(int statusCode, String body) {
        elinks.stubFor(get(urlPathMatching("/reference_data/location"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(body)
                        .withTransformers("user-token-response")));
    }
}
