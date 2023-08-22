package uk.gov.hmcts.reform.judicialapi.configuration;

import io.swagger.v3.core.filter.SpecFilter;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.customizers.OpenApiCustomiser;

public class ContentTypeFilter extends SpecFilter implements OpenApiCustomiser {

    private final String contentType;

    public ContentTypeFilter(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void customise(OpenAPI openApi) {
        openApi.getPaths().entrySet().removeIf(path -> path.getValue().readOperations().stream()
            .anyMatch(operation -> operation.getResponses().values().stream()
                .noneMatch(apiResponse -> apiResponse.getContent()
                .containsKey(this.contentType))));
        super.removeBrokenReferenceDefinitions(openApi);
    }
}