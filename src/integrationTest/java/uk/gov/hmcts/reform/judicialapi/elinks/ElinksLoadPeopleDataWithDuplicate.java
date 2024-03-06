package uk.gov.hmcts.reform.judicialapi.elinks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataExceptionRecords;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

class ElinksLoadPeopleDataWithDuplicate extends ElinksEnabledIntegrationTest {

    @Autowired
    IdamTokenConfigProperties tokenConfigProperties;

    @Autowired
    PublishSidamIdService publishSidamIdService;

    @MockBean
    ElinkTopicPublisher elinkTopicPublisher;

    @Autowired
    ElinkDataExceptionRepository elinkDataExceptionRepository;

    @AfterEach
    void cleanUp() {
        cleanupData();
    }

    @DisplayName("Test with Duplicate profiles")
    @Test
    void test_elinks_end_to_end_success_scenario_with_return_status_200()
            throws JOSEException, JsonProcessingException {

        ReflectionTestUtils.setField(elinksApiJobScheduler, "isSchedulerEnabled", true);
        ReflectionTestUtils.setField(publishSidamIdService, "elinkTopicPublisher", elinkTopicPublisher);

        dataloadSchedulerJobRepository.deleteAll();
        elinksApiJobScheduler.loadElinksJob();

        List<DataloadSchedulerJob> audits = dataloadSchedulerJobRepository.findAll();
        DataloadSchedulerJob jobDetails = audits.get(0);

        //assserting scheduler data
        assertThat(jobDetails).isNotNull();
        assertThat(jobDetails.getPublishingStatus()).isNotNull();
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(),jobDetails.getPublishingStatus());

        // asserting location data
        List<ElinkDataSchedularAudit> elinksAudit = elinkSchedularAuditRepository.findAll();
        ElinkDataSchedularAudit locationAuditEntry = elinksAudit.get(0);

        ElinkDataSchedularAudit peopleAuditEntry = elinksAudit.get(1);
        assertEquals(RefDataElinksConstants.JobStatus.PARTIAL_SUCCESS.getStatus(), peopleAuditEntry.getStatus());

        List<ElinkDataExceptionRecords> elinksException = elinkDataExceptionRepository.findAll();
        assertEquals("Personal  Code : 4925319  is already loaded",
            elinksException.get(4).getErrorDescription());
        assertEquals("Judiciary Role Name ID is invalid",
            elinksException.get(0).getErrorDescription());
        assertThat(elinksException).hasSize(5);

    }

    private void idamSetUp() {

        final String clientId = "234342332";
        final String redirectUri = "http://idam-api.aat.platform.hmcts.net";
        //The authorization and clientAuth is the dummy value which we can evaluate using BASE64 encoder.
        final String authorization = "ZHVtbXl2YWx1ZUBobWN0cy5uZXQ6SE1DVFMxMjM0";
        final String clientAuth = "cmQteHl6LWFwaTp4eXo=";
        final String url = "http://127.0.0.1:5000";
        tokenConfigProperties.setClientId(clientId);
        tokenConfigProperties.setClientAuthorization(clientAuth);
        tokenConfigProperties.setAuthorization(authorization);
        tokenConfigProperties.setRedirectUri(redirectUri);
        tokenConfigProperties.setUrl(url);

    }
}
