package uk.gov.hmcts.reform.judicialapi.docs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.judicialapi.JudicialApplication;

import java.io.File;
import java.io.FileOutputStream;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Built-in feature which saves service's swagger specs in temporary directory.
 * Each travis run on master should automatically save and upload (if updated) documentation.
 */
@SpringBootTest(classes = JudicialApplication.class, webEnvironment = MOCK)
@AutoConfigureMockMvc
class SwaggerPublisherTest {

    private static final String SWAGGER_DOC_JSON_FILE = "/tmp/swagger-specs.json";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGenerateDocs() throws Exception {

        File linuxTmpDir = new File("/tmp");
        if (!linuxTmpDir.exists()) {
            return;
        }

        byte[] specs = mockMvc.perform(get("/v2/api-docs"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        try (FileOutputStream outputStream = new FileOutputStream(SWAGGER_DOC_JSON_FILE)) {
            outputStream.write(specs);
        }
    }

}