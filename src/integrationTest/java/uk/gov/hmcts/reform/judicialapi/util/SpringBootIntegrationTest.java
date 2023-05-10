package uk.gov.hmcts.reform.judicialapi.util;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.judicialapi.JudicialApplication;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = JudicialApplication.class, webEnvironment =
        SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
public abstract class SpringBootIntegrationTest {

    @LocalServerPort
    protected int port;

}
