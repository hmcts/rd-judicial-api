package uk.gov.hmcts.reform.judicialapi.elinks.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DataloadSchedulerJobAuditTest {

    @InjectMocks
    DataloadSchedulerJobAudit dataloadSchedulerJobAudit;
    @Spy
    private DataloadSchedulerJobRepository dataloadSchedulerJobRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuditSchedulerJobStatus() {
        DataloadSchedulerJob dataloadSchedulerJob = dataloadSchedulerJobAudit
            .auditSchedulerJobStatus(convertToLocalDateTime("2023-04-12T16:42:35Z"),
                convertToLocalDateTime("2023-04-12T16:42:35Z"),"success");
        assertThat(dataloadSchedulerJob.getJobStartTime()).isEqualTo(convertToLocalDateTime("2023-04-12T16:42:35Z"));
        assertThat(dataloadSchedulerJob.getJobEndTime()).isEqualTo(convertToLocalDateTime("2023-04-12T16:42:35Z"));
        assertThat(dataloadSchedulerJob.getPublishingStatus()).isEqualTo("success");
    }

    @Test
    void testAuditSchedulerJobStatusException() {
        when(dataloadSchedulerJobRepository.save(any())).thenThrow(new RuntimeException("test exception message"));

        DataloadSchedulerJob dataloadSchedulerJob = dataloadSchedulerJobAudit
                .auditSchedulerJobStatus(convertToLocalDateTime("2023-04-12T16:42:35Z"),
                        convertToLocalDateTime("2023-04-12T16:42:35Z"),"success");

        assertThat(dataloadSchedulerJob.getJobStartTime()).isEqualTo(convertToLocalDateTime("2023-04-12T16:42:35Z"));
        assertThat(dataloadSchedulerJob.getJobEndTime()).isEqualTo(convertToLocalDateTime("2023-04-12T16:42:35Z"));
        assertThat(dataloadSchedulerJob.getPublishingStatus()).isEqualTo("success");
    }

    @Test
    void testAuditSchedulerJobStatus_withSuccess() {
        DataloadSchedulerJob dataloadSchedulerJob = new DataloadSchedulerJob(1,
            convertToLocalDateTime("2023-04-12T16:42:35Z"),
            convertToLocalDateTime("2023-04-12T16:42:35Z"),"success");
        DataloadSchedulerJob dataloadSchedulerJobResponse = dataloadSchedulerJobAudit
            .auditSchedulerJobStatus(dataloadSchedulerJob);
        assertThat(dataloadSchedulerJobResponse.getJobStartTime())
            .isEqualTo(convertToLocalDateTime("2023-04-12T16:42:35Z"));
        assertThat(dataloadSchedulerJobResponse.getJobEndTime())
            .isEqualTo(convertToLocalDateTime("2023-04-12T16:42:35Z"));
        assertThat(dataloadSchedulerJobResponse.getPublishingStatus()).isEqualTo("success");
    }

    @Test
    void testAuditSchedulerJobStatus_withSuccess_exception() {
        when(dataloadSchedulerJobRepository.save(any())).thenThrow(new RuntimeException("test exception message"));

        DataloadSchedulerJob dataloadSchedulerJob = new DataloadSchedulerJob(1,
                convertToLocalDateTime("2023-04-12T16:42:35Z"),
                convertToLocalDateTime("2023-04-12T16:42:35Z"),"success");
        DataloadSchedulerJob dataloadSchedulerJobResponse = dataloadSchedulerJobAudit
                .auditSchedulerJobStatus(dataloadSchedulerJob);

        assertThat(dataloadSchedulerJobResponse.getJobStartTime())
                .isEqualTo(convertToLocalDateTime("2023-04-12T16:42:35Z"));
        assertThat(dataloadSchedulerJobResponse.getJobEndTime())
                .isEqualTo(convertToLocalDateTime("2023-04-12T16:42:35Z"));
        assertThat(dataloadSchedulerJobResponse.getPublishingStatus()).isEqualTo("success");
    }

    private static LocalDate convertToLocalDate(String date) {
        if (Optional.ofNullable(date).isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, formatter);
        }
        return null;
    }

    private static LocalDateTime convertToLocalDateTime(String date) {
        if (Optional.ofNullable(date).isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            return LocalDateTime.parse(date, formatter);
        }
        return null;
    }

}
