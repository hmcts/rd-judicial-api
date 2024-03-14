package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinksResponses;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Location;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinksResponsesRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;

class LocationsIntegrationTest extends ElinksEnabledIntegrationTest {


    @Autowired
    LocationRepository locationRepository;

    @Autowired
    private ElinksResponsesRepository elinksResponsesRepository;

    @Autowired
    private ElinkSchedularAuditRepository elinkSchedularAuditRepository;

    @BeforeEach
    void setUp() {
        cleanupData();
    }

    @AfterEach
    void cleanUp() {
        cleanupData();
    }


    @DisplayName("Elinks location endpoint status verification")
    @Test
    void test_get_locations() {

        Map<String, Object> response = elinksReferenceDataClient.getLocations();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkLocationWrapperResponse locations = (ElinkLocationWrapperResponse) response.get("body");
        assertEquals(BASE_LOCATION_DATA_LOAD_SUCCESS, locations.getMessage());

        List<ElinksResponses> elinksResponses = elinksResponsesRepository.findAll();

        assertThat(elinksResponses.size()).isGreaterThan(0);
        assertThat(elinksResponses.get(0).getCreatedDate()).isNotNull();
        assertThat(elinksResponses.get(0).getElinksData()).isNotNull();
    }

    @DisplayName("Elinks locations verification")
    @Test
    void test_elinksService_load_location_return_status_200() {

        Map<String, Object> response = elinksReferenceDataClient.getLocations();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkLocationWrapperResponse locations = (ElinkLocationWrapperResponse) response.get("body");
        assertEquals(BASE_LOCATION_DATA_LOAD_SUCCESS, locations.getMessage());

        List<Location> locationsList = locationRepository.findAll();

        assertEquals(11, locationsList.size());
        assertEquals("1", locationsList.get(1).getRegionId());
        assertEquals("London", locationsList.get(1).getRegionDescEn());
    }

    @DisplayName("Elinks  Location to JRD Audit Functionality verification")
    @Test
    void verifyLocationJrdAuditFunctionality() {
        Map<String, Object> response = elinksReferenceDataClient.getLocations();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkLocationWrapperResponse locations = (ElinkLocationWrapperResponse) response.get("body");
        assertEquals(BASE_LOCATION_DATA_LOAD_SUCCESS, locations.getMessage());

        List<Location> locationsList = locationRepository.findAll();

        assertEquals(11, locationsList.size());
        assertEquals("1", locationsList.get(1).getRegionId());
        assertEquals("London", locationsList.get(1).getRegionDescEn());

        List<ElinkDataSchedularAudit>  elinksAudit = elinkSchedularAuditRepository.findAll();

        ElinkDataSchedularAudit auditEntry = elinksAudit.get(0);

        assertThat(auditEntry.getId()).isPositive();

        assertEquals(LOCATIONAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
    }
}
