package uk.gov.hmcts.reform.judicialapi.controller.util;

import freemarker.template.Configuration;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.ElinkEmailConfiguration;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.service.exception.EmailException;
import uk.gov.hmcts.reform.judicialapi.elinks.util.EmailTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class EmailTemplateTest {

    EmailTemplate emailTemplate;
    final Configuration freemarkerConfig = mock(Configuration.class);
    final ElinkEmailConfiguration emailConfiguration = mock(ElinkEmailConfiguration.class);

    @BeforeEach
    public void setup() {
        emailTemplate = spy(new EmailTemplate());
        setField(emailTemplate, "freemarkerConfig", freemarkerConfig);
        setField(emailTemplate, "emailConfiguration", emailConfiguration);
    }

    @Test
    @SuppressWarnings("rawtypes")
    void getMailTypeConfigTest() {
        ElinkEmailConfiguration.MailTypeConfig mailConfig = new ElinkEmailConfiguration.MailTypeConfig();

        Map<String, ElinkEmailConfiguration.MailTypeConfig> mailTypes = Map.of("lowerLevelAuth", mailConfig);

        Map<String, Object> model = getModel();

        when(emailConfiguration.getMailTypes()).thenReturn(mailTypes);
        ElinkEmailConfiguration.MailTypeConfig mailTypeConfigReturned =
                emailTemplate.getMailTypeConfig(model, "lowerLevelAuth");
        assertEquals(model, mailTypeConfigReturned.getModel());
        assertThat(Arrays.asList(mailTypeConfigReturned.getModel().get("newLowerLevelAuths"))).hasSize(1);
    }

    @Test
    void getEmailBody_should_throw_exception() throws Exception {
        when(freemarkerConfig.getTemplate(anyString())).thenThrow(IOException.class);
        Map<String, Object> model = getModel();

        assertThrows(EmailException.class,
                () -> emailTemplate.getEmailBody("lower-level-auth.ftl", model));
    }

    @NotNull
    private Map<String, Object> getModel() {
        List<Authorisation> newLowerLevelAuths = List.of(new Authorisation());
        Map<String, Object> model = new HashMap<>();
        model.put("newLowerLevelAuths", newLowerLevelAuths);
        return model;
    }

}
