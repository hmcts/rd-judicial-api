package uk.gov.hmcts.reform.judicialapi.elinks.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.ElinkEmailConfiguration;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataExceptionRecords;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.service.IEmailService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECTIDISPRESENT;

@ExtendWith(MockitoExtension.class)
public class SendEmailTest {

    @InjectMocks
    private SendEmail sendEmail;

    @Mock
    private ElinkDataExceptionRepository elinkDataExceptionRepository;

    final IEmailService emailService = spy(IEmailService.class);

    final ElinkEmailConfiguration emailConfiguration = mock(ElinkEmailConfiguration.class);

    final ElinkEmailConfiguration.MailTypeConfig config = mock(ElinkEmailConfiguration.MailTypeConfig.class);

    ElinkEmailConfiguration.MailTypeConfig mailConfig = mock(ElinkEmailConfiguration.MailTypeConfig.class);

    final EmailTemplate emailTemplate = mock(EmailTemplate.class);


    @Test
    void testSendEmailNegativeScenario() throws JsonProcessingException {

        ElinkDataExceptionRecords exceptionRecords1 = new ElinkDataExceptionRecords();
        exceptionRecords1.setId(1L);
        exceptionRecords1.setKey("key");
        exceptionRecords1.setRowId("rowId");
        exceptionRecords1.setSchedulerName("schedularName");
        exceptionRecords1.setErrorDescription("errorDescr");
        exceptionRecords1.setTableName("tableName");
        exceptionRecords1.setFieldInError(RefDataElinksConstants.OBJECT_ID);
        exceptionRecords1.setSchedulerStartTime(LocalDateTime.now());
        exceptionRecords1.setUpdatedTimeStamp(LocalDateTime.now());
        ElinkDataExceptionRecords exceptionRecords2 = new ElinkDataExceptionRecords();
        exceptionRecords2.setId(2L);
        exceptionRecords2.setKey("key1");
        exceptionRecords2.setRowId("rowId1");
        exceptionRecords2.setSchedulerName("schedularName1");
        exceptionRecords2.setErrorDescription("errorDescr1");
        exceptionRecords2.setTableName("tableName1");
        exceptionRecords2.setFieldInError(RefDataElinksConstants.OBJECT_ID);
        exceptionRecords2.setSchedulerStartTime(LocalDateTime.now());
        exceptionRecords2.setUpdatedTimeStamp(LocalDateTime.now());
        List<ElinkDataExceptionRecords> exceptionRecords = Arrays.asList(exceptionRecords1,exceptionRecords2);

        when(elinkDataExceptionRepository.findBySchedulerStartTime(any())).thenReturn(exceptionRecords);
        when(emailConfiguration.getMailTypes()).thenReturn(Map.of("key", config));

        sendEmail.sendEmail(LocalDateTime.now());

        verify(emailService, times(0)).sendEmail(any());
    }

    @Test
    void testSendEmailPositiveScenarioWithObjectIds() throws JsonProcessingException {

        ElinkDataExceptionRecords exceptionRecords1 = new ElinkDataExceptionRecords();
        exceptionRecords1.setId(1L);
        exceptionRecords1.setKey("key");
        exceptionRecords1.setRowId("rowId");
        exceptionRecords1.setSchedulerName("schedularName");
        exceptionRecords1.setErrorDescription("errorDescr");
        exceptionRecords1.setTableName("tableName");
        exceptionRecords1.setFieldInError(RefDataElinksConstants.OBJECT_ID);
        exceptionRecords1.setSchedulerStartTime(LocalDateTime.now());
        exceptionRecords1.setUpdatedTimeStamp(LocalDateTime.now());
        ElinkDataExceptionRecords exceptionRecords2 = new ElinkDataExceptionRecords();
        exceptionRecords2.setId(2L);
        exceptionRecords2.setKey("key1");
        exceptionRecords2.setRowId("rowId1");
        exceptionRecords2.setSchedulerName("schedularName1");
        exceptionRecords2.setErrorDescription(OBJECTIDISPRESENT);
        exceptionRecords2.setTableName("tableName1");
        exceptionRecords2.setFieldInError(RefDataElinksConstants.OBJECT_ID);
        exceptionRecords2.setSchedulerStartTime(LocalDateTime.now());
        exceptionRecords2.setUpdatedTimeStamp(LocalDateTime.now());
        List<ElinkDataExceptionRecords> exceptionRecords = Arrays.asList(exceptionRecords1,exceptionRecords2);

        when(elinkDataExceptionRepository.findBySchedulerStartTime(any())).thenReturn(exceptionRecords);
        when(emailConfiguration.getMailTypes()).thenReturn(Map.of("objectid", config,
                "objectidduplicate", config));
        when(config.isEnabled()).thenReturn(true);
        when(config.getBody()).thenReturn("email sample body");
        when(config.getSubject()).thenReturn("email sample subject");
        when(config.getTemplate()).thenReturn("email sample template");

        sendEmail.sendEmail(LocalDateTime.now());
        verify(emailService, times(2)).sendEmail(any());
    }

    @Test
    void testSendEmailPositiveScenarioWithBaseLocationIds() throws JsonProcessingException {

        ElinkDataExceptionRecords exceptionRecords1 = new ElinkDataExceptionRecords();
        exceptionRecords1.setId(1L);
        exceptionRecords1.setKey("key");
        exceptionRecords1.setRowId("rowId");
        exceptionRecords1.setSchedulerName("schedularName");
        exceptionRecords1.setErrorDescription("errorDescr");
        exceptionRecords1.setTableName("tableName");
        exceptionRecords1.setFieldInError(RefDataElinksConstants.BASE_LOCATION_ID);
        exceptionRecords1.setSchedulerStartTime(LocalDateTime.now());
        exceptionRecords1.setUpdatedTimeStamp(LocalDateTime.now());
        ElinkDataExceptionRecords exceptionRecords2 = new ElinkDataExceptionRecords();
        exceptionRecords2.setId(2L);
        exceptionRecords2.setKey("key1");
        exceptionRecords2.setRowId("rowId1");
        exceptionRecords2.setSchedulerName("schedularName1");
        exceptionRecords2.setErrorDescription("errorDescr1");
        exceptionRecords2.setTableName("tableName1");
        exceptionRecords2.setFieldInError(RefDataElinksConstants.BASE_LOCATION_ID);
        exceptionRecords2.setSchedulerStartTime(LocalDateTime.now());
        exceptionRecords2.setUpdatedTimeStamp(LocalDateTime.now());
        List<ElinkDataExceptionRecords> exceptionRecords = Arrays.asList(exceptionRecords1,exceptionRecords2);

        when(elinkDataExceptionRepository.findBySchedulerStartTime(any())).thenReturn(exceptionRecords);
        when(emailConfiguration.getMailTypes()).thenReturn(Map.of("baselocation", config));
        when(config.isEnabled()).thenReturn(true);
        when(config.getBody()).thenReturn("email sample body");
        when(config.getSubject()).thenReturn("email sample subject");
        when(config.getTemplate()).thenReturn("email sample template");

        sendEmail.sendEmail(LocalDateTime.now());

        verify(emailService, atLeastOnce()).sendEmail(any());
    }

    @Test
    void testSendEmailPositiveScenarioWithLocationIds() throws JsonProcessingException {

        ElinkDataExceptionRecords exceptionRecords1 = new ElinkDataExceptionRecords();
        exceptionRecords1.setId(1L);
        exceptionRecords1.setKey("key");
        exceptionRecords1.setRowId("rowId");
        exceptionRecords1.setSchedulerName("schedularName");
        exceptionRecords1.setErrorDescription("errorDescr");
        exceptionRecords1.setTableName("tableName");
        exceptionRecords1.setFieldInError(RefDataElinksConstants.LOCATION);
        exceptionRecords1.setSchedulerStartTime(LocalDateTime.now());
        exceptionRecords1.setUpdatedTimeStamp(LocalDateTime.now());
        ElinkDataExceptionRecords exceptionRecords2 = new ElinkDataExceptionRecords();
        exceptionRecords2.setId(2L);
        exceptionRecords2.setKey("key1");
        exceptionRecords2.setRowId("rowId1");
        exceptionRecords2.setSchedulerName("schedularName1");
        exceptionRecords2.setErrorDescription("errorDescr1");
        exceptionRecords2.setTableName("tableName1");
        exceptionRecords2.setFieldInError(RefDataElinksConstants.LOCATION);
        exceptionRecords2.setSchedulerStartTime(LocalDateTime.now());
        exceptionRecords2.setUpdatedTimeStamp(LocalDateTime.now());
        List<ElinkDataExceptionRecords> exceptionRecords = Arrays.asList(exceptionRecords1,exceptionRecords2);

        when(elinkDataExceptionRepository.findBySchedulerStartTime(any())).thenReturn(exceptionRecords);
        when(emailConfiguration.getMailTypes()).thenReturn(Map.of("location", config));
        when(config.isEnabled()).thenReturn(true);
        when(config.getBody()).thenReturn("email sample body");
        when(config.getSubject()).thenReturn("email sample subject");
        when(config.getTemplate()).thenReturn("email sample template");

        sendEmail.sendEmail(LocalDateTime.now());

        verify(emailService, atLeastOnce()).sendEmail(any());
    }

    @Test
    void testSendEmailPositiveScenarioWithAppointmentIds() throws JsonProcessingException {

        ElinkDataExceptionRecords exceptionRecords1 = new ElinkDataExceptionRecords();
        exceptionRecords1.setId(1L);
        exceptionRecords1.setKey("key");
        exceptionRecords1.setRowId("rowId");
        exceptionRecords1.setSchedulerName("schedularName");
        exceptionRecords1.setErrorDescription("errorDescr");
        exceptionRecords1.setTableName("tableName");
        exceptionRecords1.setFieldInError(RefDataElinksConstants.APPOINTMENTID);
        exceptionRecords1.setSchedulerStartTime(LocalDateTime.now());
        exceptionRecords1.setUpdatedTimeStamp(LocalDateTime.now());
        ElinkDataExceptionRecords exceptionRecords2 = new ElinkDataExceptionRecords();
        exceptionRecords2.setId(2L);
        exceptionRecords2.setKey("key1");
        exceptionRecords2.setRowId("rowId1");
        exceptionRecords2.setSchedulerName("schedularName1");
        exceptionRecords2.setErrorDescription("errorDescr1");
        exceptionRecords2.setTableName("tableName1");
        exceptionRecords2.setFieldInError(RefDataElinksConstants.APPOINTMENTID);
        exceptionRecords2.setSchedulerStartTime(LocalDateTime.now());
        exceptionRecords2.setUpdatedTimeStamp(LocalDateTime.now());
        List<ElinkDataExceptionRecords> exceptionRecords = Arrays.asList(exceptionRecords1,exceptionRecords2);

        when(elinkDataExceptionRepository.findBySchedulerStartTime(any())).thenReturn(exceptionRecords);
        when(emailConfiguration.getMailTypes()).thenReturn(Map.of("appointment", config));
        when(config.isEnabled()).thenReturn(true);
        when(config.getBody()).thenReturn("email sample body");
        when(config.getSubject()).thenReturn("email sample subject");
        when(config.getTemplate()).thenReturn("email sample template");

        sendEmail.sendEmail(LocalDateTime.now());

        verify(emailService, atLeastOnce()).sendEmail(any());
    }

    @Test
    void testSendEmailPositiveScenarioWithUserProfiles() throws JsonProcessingException {

        ElinkDataExceptionRecords exceptionRecords1 = new ElinkDataExceptionRecords();
        exceptionRecords1.setId(1L);
        exceptionRecords1.setKey("key");
        exceptionRecords1.setRowId("rowId");
        exceptionRecords1.setSchedulerName("schedularName");
        exceptionRecords1.setErrorDescription("errorDescr");
        exceptionRecords1.setTableName("tableName");
        exceptionRecords1.setFieldInError(RefDataElinksConstants.USER_PROFILE);
        exceptionRecords1.setSchedulerStartTime(LocalDateTime.now());
        exceptionRecords1.setUpdatedTimeStamp(LocalDateTime.now());
        ElinkDataExceptionRecords exceptionRecords2 = new ElinkDataExceptionRecords();
        exceptionRecords2.setId(2L);
        exceptionRecords2.setKey("key1");
        exceptionRecords2.setRowId("rowId1");
        exceptionRecords2.setSchedulerName("schedularName1");
        exceptionRecords2.setErrorDescription("errorDescr1");
        exceptionRecords2.setTableName("tableName1");
        exceptionRecords2.setFieldInError(RefDataElinksConstants.USER_PROFILE);
        exceptionRecords2.setSchedulerStartTime(LocalDateTime.now());
        exceptionRecords2.setUpdatedTimeStamp(LocalDateTime.now());
        List<ElinkDataExceptionRecords> exceptionRecords = Arrays.asList(exceptionRecords1,exceptionRecords2);

        when(elinkDataExceptionRepository.findBySchedulerStartTime(any())).thenReturn(exceptionRecords);
        when(emailConfiguration.getMailTypes()).thenReturn(Map.of("userprofile", config));
        when(config.isEnabled()).thenReturn(true);
        when(config.getBody()).thenReturn("email sample body");
        when(config.getSubject()).thenReturn("email sample subject");
        when(config.getTemplate()).thenReturn("email sample template");

        sendEmail.sendEmail(LocalDateTime.now());

        verify(emailService, atLeastOnce()).sendEmail(any());
    }

}
