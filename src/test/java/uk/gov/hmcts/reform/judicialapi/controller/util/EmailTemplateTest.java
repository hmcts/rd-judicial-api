package uk.gov.hmcts.reform.judicialapi.controller.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.ElinkEmailConfiguration;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.service.exception.EmailException;
import uk.gov.hmcts.reform.judicialapi.elinks.util.EmailTemplate;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class EmailTemplateTest {

    private static final String LOWER_LEVEL_AUTH = "lowerLevelAuth";
    private static final String NEW_LOWER_LEVEL_AUTHS = "newLowerLevelAuths";
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String VALID_TEMPLATE = "validTemplate.ftl";

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

        Map<String, ElinkEmailConfiguration.MailTypeConfig> mailTypes = Map.of(LOWER_LEVEL_AUTH, mailConfig);

        Map<String, Object> model = getModel();

        when(emailConfiguration.getMailTypes()).thenReturn(mailTypes);
        ElinkEmailConfiguration.MailTypeConfig mailTypeConfigReturned =
                emailTemplate.getMailTypeConfig(model, LOWER_LEVEL_AUTH);
        assertEquals(model, mailTypeConfigReturned.getModel());
        assertThat(Arrays.asList(mailTypeConfigReturned.getModel().get(NEW_LOWER_LEVEL_AUTHS))).hasSize(1);
    }

    @Test
    void getEmailBody_should_throw_exception() throws Exception {
        when(freemarkerConfig.getTemplate(anyString())).thenThrow(IOException.class);
        Map<String, Object> model = getModel();

        assertThrows(EmailException.class,
                () -> emailTemplate.getEmailBody("lower-level-auth.ftl", model));
    }

    @Test
    void shouldReturnEmailBodyWhenTemplateIsValid() throws IOException, TemplateException {
        Map<String, Object> model = Map.of(KEY, VALUE);
        Template mockTemplate = mock(Template.class);
        when(freemarkerConfig.getTemplate(VALID_TEMPLATE)).thenReturn(mockTemplate);
        doAnswer(invocation -> {
            Writer writer = invocation.getArgument(1);
            writer.write("Email body content");
            return null;
        }).when(mockTemplate).process(any(), any());

        String emailBody = emailTemplate.getEmailBody(VALID_TEMPLATE, model);

        assertNotNull(emailBody);
        assertFalse(emailBody.isEmpty());
    }

    @Test
    void shouldThrowEmailExceptionWhenModelIsNull() throws IOException, TemplateException {
        Template mockTemplate = mock(Template.class);
        when(freemarkerConfig.getTemplate(VALID_TEMPLATE)).thenReturn(mockTemplate);
        doThrow(new TemplateException("Model is null", null)).when(mockTemplate).process(any(), any());

        assertThrows(EmailException.class, () -> emailTemplate.getEmailBody(VALID_TEMPLATE, null));
    }

    @NotNull
    private Map<String, Object> getModel() {
        List<Authorisation> newLowerLevelAuths = List.of(new Authorisation());
        Map<String, Object> model = new HashMap<>();
        model.put(NEW_LOWER_LEVEL_AUTHS, newLowerLevelAuths);
        return model;
    }

}
