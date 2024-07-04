package uk.gov.hmcts.reform.judicialapi.util;

import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.ImageNameSubstitutor;

public class ContainerImageNameSubstitutor extends ImageNameSubstitutor {

    private final DockerImageName hmctsPostgresDockerImage = DockerImageName
            .parse("hmctspublic.azurecr.io/imported/postgres:16-alpine")
            .asCompatibleSubstituteFor("postgres");

    @Override
    public DockerImageName apply(DockerImageName original) {

        if (original.asCanonicalNameString().contains("postgres")) {
            return hmctsPostgresDockerImage;
        }

        return original;
    }

    @Override
    protected String getDescription() {
        return "hmcts acr substitutor";
    }
}