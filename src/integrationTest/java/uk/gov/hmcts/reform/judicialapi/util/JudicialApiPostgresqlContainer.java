package uk.gov.hmcts.reform.judicialapi.util;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

public class JudicialApiPostgresqlContainer extends PostgreSQLContainer<JudicialApiPostgresqlContainer> {

    private static final DockerImageName hmctsPostgresDockerImage = DockerImageName
            .parse("hmctspublic.azurecr.io/imported/postgres:11.1")
            .asCompatibleSubstituteFor("postgres");

    private JudicialApiPostgresqlContainer() {
        super(hmctsPostgresDockerImage);
    }

    @Container
    private static final JudicialApiPostgresqlContainer container = new JudicialApiPostgresqlContainer();

}
