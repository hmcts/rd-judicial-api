package uk.gov.hmcts.reform.judicialapi;

import com.google.common.collect.ImmutableList;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;

@RunWith(SpringIntegrationSerenityRunner.class)
@ActiveProfiles("functional")
@Slf4j
public class RetrieveJudicialUserProfileByEmailTest extends AuthorizationFunctionalTest {

    @BeforeClass
    public static void dbSetup() throws Exception {
        String loadFile = ResourceUtils.getFile("classpath:load-data-functional.sql").getCanonicalPath();
        executeScript(ImmutableList.of(Paths.get(loadFile)));
    }

    @AfterClass
    public static void dbTearDown() throws Exception {
        String deleteFile = ResourceUtils.getFile("classpath:delete-data-functional.sql").getCanonicalPath();
        executeScript(ImmutableList.of(Paths.get(deleteFile)));
    }
}


