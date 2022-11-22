package uk.gov.hmcts.reform.judicialapi.util;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class JudicialApiPostgresqlContainer extends PostgreSQLContainer<JudicialApiPostgresqlContainer> {
    private static final String IMAGE_VERSION = "postgres:11.1";

    private JudicialApiPostgresqlContainer() {
        super(IMAGE_VERSION);
    }

    @Container
    private static final JudicialApiPostgresqlContainer container = new JudicialApiPostgresqlContainer();

}
