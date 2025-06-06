package uk.gov.hmcts.reform.judicialapi.docs;

import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Built-in feature which saves service's swagger specs in temporary directory.
 * Each travis run on master should automatically save and upload (if updated) documentation.
 */
@WithTags({@WithTag("testType:Integration")})
class SwaggerPublisherTest extends AuthorizationEnabledIntegrationTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {


        this.mvc = webAppContextSetup(webApplicationContext)

                .build();
    }

    @DisplayName("Generate swagger documentation")
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void generateDocs() throws Exception {
        byte[] specs = mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        try (OutputStream outputStream = Files.newOutputStream(Paths.get("/tmp/openapi-specs.json"))) {
            outputStream.write(specs);
        }

    }

}
