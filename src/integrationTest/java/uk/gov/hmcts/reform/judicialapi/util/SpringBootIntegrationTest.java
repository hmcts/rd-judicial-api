package uk.gov.hmcts.reform.judicialapi.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.judicialapi.JudicialApplication;
import uk.gov.hmcts.reform.judicialapi.configuration.RestTemplateConfiguration;
import uk.gov.hmcts.reform.judicialapi.wiremock.WireMockContextInitializer;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@ExtendWith({SpringExtension.class, SerenityJUnit5Extension.class})
@SpringBootTest(classes = JudicialApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ContextConfiguration(classes = RestTemplateConfiguration.class, initializers = WireMockContextInitializer.class)
@WithTags({@WithTag("testType:Integration")})
public abstract class SpringBootIntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestApplicationServer testApplicationServer;

    public static ObjectMapper getObjectMapper() {

        return new ObjectMapper()
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

}
