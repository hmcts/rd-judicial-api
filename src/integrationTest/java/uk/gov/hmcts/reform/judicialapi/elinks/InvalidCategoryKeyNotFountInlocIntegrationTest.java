package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.scheduler.ElinksApiJobScheduler;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvalidCategoryKeyNotFountInlocIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    IdamTokenConfigProperties tokenConfigProperties;

    @Autowired
    private ElinkSchedularAuditRepository elinkSchedularAuditRepository;

    @Autowired
    private ElinksApiJobScheduler elinksApiJobScheduler;

    @Autowired
    private DataloadSchedulerJobRepository dataloadSchedulerJobRepository;

    @Autowired
    PublishSidamIdService publishSidamIdService;

    @MockBean
    ElinkTopicPublisher elinkTopicPublisher;

    @Autowired
    ElinkDataExceptionRepository elinkDataExceptionRepository;

    @BeforeEach
    void before() {
        cleanupData();
    }

    @AfterEach
    void cleanUp() {
        cleanupData();
    }

    @DisplayName("Elinks People endpoint when BaseLocation ID Invalid in location Type verification")
    @Test
    void getPeopleUserProfile() {

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse)response.get("body");
        assertEquals("People data loaded successfully", profiles.getMessage());

        var list = elinkDataExceptionRepository.findAll();

        assertThat(list).isNotEmpty();
        Assert.assertEquals("Appointment's Base Location ID : 768  is not available in location_type table",
                list.get(0).getErrorDescription());
    }
}
