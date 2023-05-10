package uk.gov.hmcts.reform.judicialapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import uk.gov.hmcts.reform.judicialapi.versions.V1;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket swaggerApiV2() {

        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("v2")
            .genericModelSubstitutes(Optional.class)
            .select()
            .apis(p -> {
                if (p.produces() != null) {
                    for (MediaType mt : p.produces()) {
                        if (mt.toString().equals(V2.MediaType.SERVICE)) {
                            return true;
                        }
                    }
                }
                return false;
            })
            .build()
            .produces(Collections.singleton(V2.MediaType.SERVICE))
            .apiInfo(new ApiInfoBuilder().version("v2").title("JRD API").description("JRD API v2").build())
            .securitySchemes(apiKeyList());
    }


    @Bean
    public Docket swaggerApiV1() {

        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("v1")
            .genericModelSubstitutes(Optional.class)
            .select()
            .apis(p -> {
                if (p.produces() != null) {
                    for (MediaType mt : p.produces()) {
                        if (mt.toString().equals(V1.MediaType.SERVICE)) {
                            return true;
                        }
                    }
                }
                return false;
            })
            .build()
            .produces(Collections.singleton(V1.MediaType.SERVICE))
            .apiInfo(new ApiInfoBuilder().version("v1").title("JRD API").description("JRD API v1").build())
            .securitySchemes(apiKeyList());

    }



    private List<ApiKey> apiKeyList() {
        return
           newArrayList(
               new ApiKey("Authorization", "Authorization","header"),
               new ApiKey("ServiceAuthorization", "ServiceAuthorization", "header"),
               new ApiKey("UserEmail", "UserEmail", "header")
           );
    }

}
