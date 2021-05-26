import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class FetchUsersIntegrationTest extends AuthorizationEnabledIntegrationTest {

    private UserRequest userRequest;

    @Before
    public void setUp() {
        super.setUpClient();
        userRequest = new UserRequest(Arrays.asList("44862987-4b00-e2e7-4ff8-281b87f16bf9",
                "4c0ff6a3-8fd6-803b-301a-29d9dacccca8"));
    }

    @Test
    public void shouldReturn200WithValidParameters() {
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, "jrd-system-user", false);
        assertThat(response).containsEntry("http_status", "200 OK");
    }

    @Test
    public void shouldReturn403ForUnauthorisedUsers() {
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, "test-user-role", false);
        assertThat(response).containsEntry("http_status", "403");
    }

    @Test
    public void shouldReturn401ForInvalidTokens() {
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, "jrd-system-user", true);
        assertThat(response).containsEntry("http_status", "401");
    }

    @Test
    public void shouldReturn400ForEmptyUserIds() {
        userRequest = new UserRequest();
        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, "jrd-system-user", false);
        assertThat(response).containsEntry("http_status", "400");
    }

    @Test
    public void shouldReturn404WhenNoUsersFound() {
        userRequest = new UserRequest(Collections.singletonList(UUID.randomUUID().toString()));

        Map<String, Object> response = judicialReferenceDataClient.fetchJudicialProfilesById(10, 0,
                userRequest, "jrd-system-user", false);
        assertThat(response).containsEntry("http_status", "404");
    }
}
