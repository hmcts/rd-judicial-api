package uk.gov.hmcts.reform.judicialapi.util;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.judicialapi.persistence.JudicialRoleTypeRepository;
import uk.gov.hmcts.reform.judicialapi.persistence.JudicialUserProfileRepository;

@Configuration
@TestPropertySource(properties = {"S2S_URL=http://127.0.0.1:8990"})
public abstract class AuthorizationEnabledIntegrationTest extends SpringBootIntegrationTest {

    @Autowired
    protected JudicialRoleTypeRepository judicialRoleTypeRepository;

    @Autowired
    protected JudicialUserProfileRepository judicialUserProfileRepository;

    protected JudicialReferenceDataClient judicialReferenceDataClient;

    @Value("exui.role.pui-case-worker")
    protected String caseworker;

    @Rule
    public WireMockRule s2sService = new WireMockRule(8990);

    @Before
    public void setUpClient() {
        judicialReferenceDataClient = new JudicialReferenceDataClient(port);
    }

    @Before
    public void setUpWireMock() throws Exception {

        s2sService.stubFor(get(urlEqualTo("/details"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("it")));
    }

    @After
    public void cleanupTestData() {
        //judicialRoleTypeRepository.deleteAll();;
    }


}
