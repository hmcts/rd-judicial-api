package uk.gov.hmcts.reform.judicialapi;

import static java.lang.System.getenv;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.judicialapi.client.JudicialApiClient;
import uk.gov.hmcts.reform.judicialapi.client.S2sClient;
import uk.gov.hmcts.reform.judicialapi.config.Oauth2;
import uk.gov.hmcts.reform.judicialapi.config.TestConfigProperties;
import uk.gov.hmcts.reform.judicialapi.idam.IdamOpenIdClient;

@RunWith(SpringIntegrationSerenityRunner.class)
@ContextConfiguration(classes = {TestConfigProperties.class, Oauth2.class})
@ComponentScan("uk.gov.hmcts.reform.judicialapi")
@TestPropertySource("classpath:application-functional.yaml")
@Slf4j
@Ignore
public abstract class AuthorizationFunctionalTest {

    @Value("${s2s-url}")
    protected String s2sUrl;

    @Value("${s2s-name}")
    protected String s2sName;

    @Value("${s2s-secret}")
    protected String s2sSecret;

    @Value("${targetInstance}")
    protected String judicialApiUrl;

    @Value("${exui.role.caseworker}")
    protected String caseworker;

    @Value("${exui.role.pui-organisation-manager}")
    protected String puiOrgManager;

    protected JudicialApiClient judicialApiClient;

    @Autowired
    protected TestConfigProperties configProperties;

    @Before
    public void setUp() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.defaultParser = Parser.JSON;

        log.info("Configured S2S secret: " + s2sSecret.substring(0, 2) + "************" + s2sSecret.substring(14));
        log.info("Configured S2S microservice: " + s2sName);
        log.info("Configured S2S URL: " + s2sUrl);

        /*SerenityRest.proxy("proxyout.reform.hmcts.net", 8080);
        RestAssured.proxy("proxyout.reform.hmcts.net", 8080);*/

        IdamOpenIdClient idamOpenIdClient = new IdamOpenIdClient(configProperties);
        log.info("idamOpenIdClient: " + idamOpenIdClient);
        String s2sToken = new S2sClient(s2sUrl, s2sName, s2sSecret).signIntoS2S();
        judicialApiClient = new JudicialApiClient(judicialApiUrl, s2sToken, idamOpenIdClient);
    }

    protected static void executeScript(List<Path> scriptFiles) throws SQLException, IOException {

        log.info("environment script execution started::");
        try (Connection connection = createDataSource().getConnection()) {
            try (Statement statement = connection.createStatement()) {
                for (Path path : scriptFiles) {
                    for (String scriptLine : Files.readAllLines(path)) {
                        statement.addBatch(scriptLine);
                    }
                    statement.executeBatch();
                }
            }
        } catch (Exception exe) {
            log.error("FunctionalTestSuite script execution error with script ::" + exe.toString());
            throw exe;
        }
        log.info("environment script execution completed::");
    }

    private static DataSource createDataSource() {

        log.info("DB Host name::" + getValueOrDefault("POSTGRES_HOST", "localhost"));
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(getValueOrDefault("POSTGRES_HOST", "localhost"));
        dataSource.setPortNumber(Integer.parseInt(getValueOrDefault("POSTGRES_PORT", "5456")));
        dataSource.setDatabaseName(getValueOrDefault("POSTGRES_DATABASE","dbjuddata"));
        dataSource.setUser("dbjuddata");
        dataSource.setPassword("dbjuddata");
        return dataSource;
    }

    private static String getValueOrDefault(String name, String defaultValue) {
        String value = getenv(name);
        return value != null ? value : defaultValue;
    }

    private static String getValueOrThrow(String name) {
        String value = getenv(name);
        if (value == null) {
            throw new IllegalArgumentException("Environment variable '" + name + "' is missing");
        }
        return value;
    }

    @After
    public void tearDown() {
    }

}
