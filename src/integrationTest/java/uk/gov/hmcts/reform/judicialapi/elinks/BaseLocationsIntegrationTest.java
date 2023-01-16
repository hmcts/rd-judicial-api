package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;

class BaseLocationsIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    BaseLocationRepository baseLocationRepository;


    @BeforeEach
    void setUp() {

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

        assertEquals(1, baseLocationList.size());
        assertEquals("0", baseLocationList.get(0).getBaseLocationId());
        assertEquals("default", baseLocationList.get(0).getCourtName());
        assertEquals("default", baseLocationList.get(0).getCourtType());
        assertEquals("default", baseLocationList.get(0).getCircuit());
        assertEquals("default", baseLocationList.get(0).getAreaOfExpertise());
    }

}
