package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.ElinksController;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Location;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATION_DATA_LOAD_SUCCESS;

class LocationsIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    ElinksController elinksController;

    @Autowired
    LocationRepository locationRepository;


    @BeforeEach
    void setUp() {

    }

    @Test
    void test_elinksController_loaded() {
        assertThat(elinksController).isNotNull();
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
}
