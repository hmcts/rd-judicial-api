package uk.gov.hmcts.reform.judicialapi.elinks.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.ElinkEmailConfiguration;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataExceptionRecords;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.service.IEmailService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.dto.Email;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.APPOINTMENTID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_ID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.CONTENT_TYPE_HTML;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DATE_PATTERN;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATION;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECTIDISPRESENT;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECT_ID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.USER_PROFILE;

@Component
@Slf4j
public class SendEmail {

    @Autowired
    ElinkDataExceptionRepository elinkDataExceptionRepository;

    @Autowired
    EmailTemplate emailTemplate;

    @Autowired
    ElinkEmailConfiguration emailConfiguration;

    @Value("${logging-component-name}")
    private String logComponentName;

    @Autowired
    IEmailService emailService;


    public void sendEmail(LocalDateTime schedulerStartTime) {
        List<ElinkDataExceptionRecords> list = elinkDataExceptionRepository
                .findBySchedulerStartTime(schedulerStartTime);

        Map<String, List<ElinkDataExceptionRecords>> map = list.stream()
                .collect(Collectors.groupingBy(ElinkDataExceptionRecords::getFieldInError));

        if (map.containsKey(BASE_LOCATION_ID)) {
            sendEmail(new HashSet<>(map.get(BASE_LOCATION_ID)), "baselocation",
                    LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        }
        if (map.containsKey(LOCATION)) {
            sendEmail(new HashSet<>(map.get(LOCATION)), "location",
                    LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        }
        if (map.containsKey(APPOINTMENTID)) {
            sendEmail(new HashSet<>(map.get(APPOINTMENTID)), "appointment",
                    LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        }
        if (map.containsKey(USER_PROFILE)) {
            sendEmail(new HashSet<>(map.get(USER_PROFILE)), "userprofile",
                    LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        }
        if (map.containsKey(OBJECT_ID)) {
            Map<Boolean, List<ElinkDataExceptionRecords>> errorDescMap = map.get(OBJECT_ID).stream()
                    .collect(Collectors.partitioningBy(a -> OBJECTIDISPRESENT.equals(a.getErrorDescription())));
            if (!errorDescMap.get(true).isEmpty()) {
                sendEmail(new HashSet<>(errorDescMap.get(true)), "objectidduplicate",
                        LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
            }
            if (!errorDescMap.get(false).isEmpty()) {
                sendEmail(new HashSet<>(errorDescMap.get(false)), "objectid",
                        LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
            }
        }
    }

    public int sendEmail(Set<ElinkDataExceptionRecords> data, String type, Object... params) {
        log.info("{} : send Email",logComponentName);
        ElinkEmailConfiguration.MailTypeConfig config = emailConfiguration.getMailTypes()
                .get(type);
        if (config != null && config.isEnabled()) {
            Email email = uk.gov.hmcts.reform.judicialapi.elinks.service.dto.Email.builder()
                    .contentType(CONTENT_TYPE_HTML)
                    .from(config.getFrom())
                    .to(config.getTo())
                    .subject(String.format(config.getSubject(), params))
                    .messageBody(emailTemplate.getEmailBody(config.getTemplate(), Map.of("resultsRequest", data)))
                    .build();
            return emailService.sendEmail(email);
        }
        return -1;
    }


}

