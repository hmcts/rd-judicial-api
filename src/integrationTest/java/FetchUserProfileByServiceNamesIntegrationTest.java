import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.util.JudicialReferenceDataClient;
import uk.gov.hmcts.reform.judicialapi.util.RefDataConstants;

import java.util.Map;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class FetchUserProfileByServiceNamesIntegrationTest extends AuthorizationEnabledIntegrationTest {

    @Before
    public void setUpClient() {
        super.setUpClient();
    }

    @BeforeClass
    public static void setup() {
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @AfterClass
    public static void tearDown() {
        JudicialReferenceDataClient.setBearerToken(EMPTY);
    }

    @Test
    public void shouldReturn400ForEmptyServiceName() {
        Map<String, Object> response = judicialReferenceDataClient
                .fetchUserProfileByServiceName("", null, null,
                        "", "", "jrd-system-user");
        assertThat(response).containsEntry("http_status", "400");
        assertTrue(response.get("response_body").toString()
                .contains("Required request parameter 'serviceName' for method parameter type String is not present"));
    }

    @Test
    public void shouldReturn400ForInvalidPageSize() {
        Map<String, Object> response = judicialReferenceDataClient
                .fetchUserProfileByServiceName("cmc", -1, null,
                        "", "", "jrd-system-user");
        assertThat(response).containsEntry("http_status", "400");
        assertTrue(response.get("response_body").toString()
                .contains(String.format(RefDataConstants.INVALID_FIELD, RefDataConstants.PAGE_SIZE)));
    }

    @Test
    public void shouldReturn400ForInvalidPageNumber() {
        Map<String, Object> response = judicialReferenceDataClient
                .fetchUserProfileByServiceName("cmc", 1, -1,
                        "", "", "jrd-system-user");
        assertThat(response).containsEntry("http_status", "400");
        assertTrue(response.get("response_body").toString()
                .contains(String.format(RefDataConstants.INVALID_FIELD, RefDataConstants.PAGE_NUMBER)));
    }

    @Test
    public void shouldReturn400ForInvalidSortDirection() {
        Map<String, Object> response = judicialReferenceDataClient
                .fetchUserProfileByServiceName("cmc", 1, 1,
                        "Invalid", "", "jrd-system-user");
        assertThat(response).containsEntry("http_status", "400");
        assertTrue(response.get("response_body").toString()
                .contains(String.format(RefDataConstants.INVALID_FIELD, RefDataConstants.SORT_DIRECTION)));
    }
}
