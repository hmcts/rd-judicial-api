package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASELOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;

class BaseLocationsIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    BaseLocationRepository baseLocationRepository;

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

        Map<String, Object> response = elinksReferenceDataClient.getBaseLocations();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkBaseLocationWrapperResponse baseLocations = (ElinkBaseLocationWrapperResponse) response.get("body");
        assertEquals(BASE_LOCATION_DATA_LOAD_SUCCESS, baseLocations.getMessage());
    }

    @DisplayName("Elinks base locations verification")
    @Test
    void test_elinksService_load_base_location_return_status_200() {

        Map<String, Object> response = elinksReferenceDataClient.getBaseLocations();
        assertThat(response).containsEntry("http_status", "200 OK");
        ElinkBaseLocationWrapperResponse locations = (ElinkBaseLocationWrapperResponse) response.get("body");
        assertEquals(BASE_LOCATION_DATA_LOAD_SUCCESS, locations.getMessage());

        List<BaseLocation> baseLocationList = baseLocationRepository.findAll();

        assertEquals(7, baseLocationList.size());
        assertEquals("Aberconwy",baseLocationList.get(0).getCourtName());
        assertEquals("1029",baseLocationList.get(0).getBaseLocationId());
        assertEquals("Old Gwynedd",baseLocationList.get(0).getCourtType());
    }

    @DisplayName("Elinks Base Location to JRD Audit Functionality verification")
    @Test
    void verifyBaseLocationJrdAuditFunctionality() {

        Map<String, Object> baseLocationsResponse = elinksReferenceDataClient.getBaseLocations();
        assertThat(baseLocationsResponse).containsEntry("http_status", "200 OK");
        ElinkBaseLocationWrapperResponse profiles = (ElinkBaseLocationWrapperResponse)baseLocationsResponse.get("body");
        assertEquals(BASE_LOCATION_DATA_LOAD_SUCCESS, profiles.getMessage());

        List<BaseLocation> baseLocationList = baseLocationRepository.findAll();

        assertEquals(7, baseLocationList.size());

        assertEquals("Prestatyn Civil and Family Justice Centre",baseLocationList.get(4).getCourtName());
        assertEquals("1192",baseLocationList.get(4).getBaseLocationId());
        assertEquals("County Court",baseLocationList.get(4).getCourtType());

        List<ElinkDataSchedularAudit>  elinksAudit = elinkSchedularAuditRepository.findAll();

        ElinkDataSchedularAudit auditEntry = elinksAudit.get(0);


        assertThat(auditEntry.getId()).isPositive();
        assertEquals(BASELOCATIONAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
    }

    private void cleanupData() {
        elinkSchedularAuditRepository.deleteAll();
    }

}
