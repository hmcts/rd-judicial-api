package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PeopleAppointmentTypeNullIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    IdamTokenConfigProperties tokenConfigProperties;

    @Autowired
    PublishSidamIdService publishSidamIdService;

    @MockBean
    ElinkTopicPublisher elinkTopicPublisher;

    @BeforeEach
    void before() {
        cleanupData();
    }

    @AfterEach
    void cleanUp() {
        cleanupData();
    }

    @DisplayName("Elinks People endpoint appointment Type Null verification")
    @Test
    void getPeopleUserProfile() {
        elinksApiJobScheduler.loadElinksJob();

        List<ElinkDataSchedularAudit> elinksAudit = elinkSchedularAuditRepository.findAll();
        // asserting location data
        validateLocationData(elinksAudit);
        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse)response.get("body");
        assertEquals("People data loaded successfully", profiles.getMessage());

        var list = elinkDataExceptionRepository.findAll();

        assertThat(list).isNotEmpty();
        Assert.assertEquals("The Type field is null for the given Appointment.",
                list.get(0).getErrorDescription());

    }

    private void validateLocationData(List<ElinkDataSchedularAudit> elinksAudit) {
        cleanupData();
        Map<String, Object> locationResponse = elinksReferenceDataClient.getLocations();
        ElinkLocationWrapperResponse locations = (ElinkLocationWrapperResponse) locationResponse.get("body");

    }
}