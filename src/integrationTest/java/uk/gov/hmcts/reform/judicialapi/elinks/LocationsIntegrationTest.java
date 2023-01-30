package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Location;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATION_DATA_LOAD_SUCCESS;

class LocationsIntegrationTest extends ElinksEnabledIntegrationTest {


    @Autowired
    LocationRepository locationRepository;


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
        assertEquals(LOCATION_DATA_LOAD_SUCCESS, locations.getMessage());
    }

    @DisplayName("Elinks locations verification")
    @Test
    void test_elinksService_load_location_return_status_200() {

        Map<String, Object> response = elinksReferenceDataClient.getLocations();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkLocationWrapperResponse locations = (ElinkLocationWrapperResponse) response.get("body");
        assertEquals(LOCATION_DATA_LOAD_SUCCESS, locations.getMessage());

        List<Location> locationsList = locationRepository.findAll();

        assertEquals(1, locationsList.size());
        assertEquals("0", locationsList.get(0).getRegionId());
        assertEquals("default", locationsList.get(0).getRegionDescEn());
        assertEquals("default", locationsList.get(0).getRegionDescCy());
    }

    @DisplayName("Elinks  Location to JRD Audit Functionality verification")
    @Test
    void verifyLocationJrdAuditFunctionality() {
        Map<String, Object> response = elinksReferenceDataClient.getLocations();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkLocationWrapperResponse locations = (ElinkLocationWrapperResponse) response.get("body");
        assertEquals(LOCATION_DATA_LOAD_SUCCESS, locations.getMessage());

        List<Location> locationsList = locationRepository.findAll();

        assertEquals(1, locationsList.size());
        assertEquals("0", locationsList.get(0).getRegionId());
        assertEquals("default", locationsList.get(0).getRegionDescEn());
        assertEquals("default", locationsList.get(0).getRegionDescCy());

        List<ElinkDataSchedularAudit>  elinksAudit = elinkSchedularAuditRepository.findAll();

        ElinkDataSchedularAudit auditEntry = elinksAudit.get(0);

        assertThat(auditEntry.getId()).isPositive();

        assertEquals(LOCATIONAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
    }

    private void cleanupData() {
        elinkSchedularAuditRepository.deleteAll();
    }

}
