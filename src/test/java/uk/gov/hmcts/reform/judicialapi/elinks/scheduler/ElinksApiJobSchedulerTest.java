package uk.gov.hmcts.reform.judicialapi.elinks.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkDeletedWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.SchedulerJobStatusResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.impl.ELinksServiceImpl;
import uk.gov.hmcts.reform.judicialapi.elinks.util.DataloadSchedulerJobAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataExceptionHelper;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataIngestionSchedularAudit;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.JobStatus.IN_PROGRESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.JobStatus.SUCCESS;

@ExtendWith(MockitoExtension.class)
class ElinksApiJobSchedulerTest {

    private static final String WRAPPER_BASE_URL = "http://localhost:8093";

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private DataloadSchedulerJobAudit dataloadSchedulerJobAudit;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ElinkDataIngestionSchedularAudit elinkDataIngestionSchedularAudit;

    @Mock
    private ElinkDataExceptionHelper elinkDataExceptionHelper;

    @Mock
    private DataloadSchedulerJobRepository dataloadSchedulerJobRepository;

    @Mock
    private ELinksServiceImpl elinksServiceImpl;

    @InjectMocks
    @Spy
    private ElinksApiJobScheduler elinksApiJobScheduler;

    @Test
    void shouldConfigureExpectedPropertyKeysForSchedulerSteps() throws NoSuchFieldException {
        assertValueProperty("isloadLocationEnabled", "${elinks.scheduler.steps.loadLocation.enabled:false}");
        assertValueProperty("isloadPersonEnabled", "${elinks.scheduler.steps.loadPerson.enabled:false}");
        assertValueProperty("isloadDeletedEnabled", "${elinks.scheduler.steps.loadDeleted.enabled:false}");
        assertValueProperty("isloadLeaversEnabled", "${elinks.scheduler.steps.loadLeavers.enabled:false}");
        assertValueProperty("isidamElasticSearchEnabled", "${elinks.scheduler.steps.idamElasticSearch.enabled:false}");
        assertValueProperty("ispublishSidamIdToAsbEnabled",
            "${elinks.scheduler.steps.publishSidamIdToAsb.enabled:false}");
        assertValueProperty("isfetchIdamIdsEnabled", "${elinks.scheduler.steps.fetchIdamIds.enabled:false}");
    }

    @Test
    void shouldNotInvokeSchedulerStepsWhenAllStepFlagsAreDisabled() {
        setBaseUrl();

        elinksApiJobScheduler.loadElinksData();

        verify(elinksApiJobScheduler, never()).retrieveLocationDetails();
        verify(elinksApiJobScheduler, never()).retrievePeopleDetails();
        verify(elinksApiJobScheduler, never()).retrieveLeaversDetails();
        verify(elinksApiJobScheduler, never()).retrieveDeletedDetails();
        verify(elinksApiJobScheduler, never()).retrieveIdamElasticSearchDetails();
        verify(elinksApiJobScheduler, never()).retrieveAsbPublishDetails();
        verify(restTemplate, never()).exchange(any(String.class), eq(HttpMethod.GET), any(), eq(Object.class));
        verify(elinksServiceImpl).cleanUpElinksResponses();
        verify(elinksServiceImpl).deleteJohProfiles(any());
    }

    @Test
    void shouldInvokeEveryEnabledSchedulerStep() {
        setBaseUrl();
        enableAllStepFlags();

        doReturn(ResponseEntity.ok(new ElinkLocationWrapperResponse()))
            .when(elinksApiJobScheduler).retrieveLocationDetails();
        doReturn(ResponseEntity.ok(new ElinkPeopleWrapperResponse()))
            .when(elinksApiJobScheduler).retrievePeopleDetails();
        doReturn(ResponseEntity.ok(new ElinkLeaversWrapperResponse()))
            .when(elinksApiJobScheduler).retrieveLeaversDetails();
        doReturn(ResponseEntity.ok(new ElinkDeletedWrapperResponse()))
            .when(elinksApiJobScheduler).retrieveDeletedDetails();
        doReturn(ResponseEntity.ok(new Object()))
            .when(elinksApiJobScheduler).retrieveIdamElasticSearchDetails();
        doReturn(ResponseEntity.ok(new SchedulerJobStatusResponse()))
            .when(elinksApiJobScheduler).retrieveAsbPublishDetails();
        when(restTemplate.exchange(
            eq(WRAPPER_BASE_URL + ElinksApiJobScheduler.ELINKS_CONTROLLER_BASE_URL + "/idam/find"),
            eq(HttpMethod.GET),
            any(),
            eq(Object.class))
        ).thenReturn(ResponseEntity.ok(new Object()));

        elinksApiJobScheduler.loadElinksData();

        verify(elinksApiJobScheduler).retrieveLocationDetails();
        verify(elinksApiJobScheduler).retrievePeopleDetails();
        verify(elinksApiJobScheduler).retrieveLeaversDetails();
        verify(elinksApiJobScheduler).retrieveDeletedDetails();
        verify(elinksApiJobScheduler).retrieveIdamElasticSearchDetails();
        verify(elinksApiJobScheduler).retrieveAsbPublishDetails();
        verify(restTemplate).exchange(
            eq(WRAPPER_BASE_URL + ElinksApiJobScheduler.ELINKS_CONTROLLER_BASE_URL + "/idam/find"),
            eq(HttpMethod.GET),
            any(),
            eq(Object.class));
        verify(elinksServiceImpl).cleanUpElinksResponses();
        verify(elinksServiceImpl).deleteJohProfiles(any());
    }

    @Test
    void shouldLoadSchedulerJobWhenSchedulerIsEnabled() {
        ReflectionTestUtils.setField(elinksApiJobScheduler, "isSchedulerEnabled", true);
        when(dataloadSchedulerJobRepository.findFirstByOrderByIdDesc()).thenReturn(null);
        doNothing().when(elinksApiJobScheduler).loadElinksData();
        DataloadSchedulerJob savedAudit = new DataloadSchedulerJob();
        savedAudit.setId(11);
        savedAudit.setPublishingStatus(IN_PROGRESS.getStatus());
        when(dataloadSchedulerJobAudit.auditSchedulerJobStatus(any(DataloadSchedulerJob.class))).thenReturn(savedAudit);
        when(dataloadSchedulerJobRepository.findFirstByOrderByIdDesc()).thenReturn(savedAudit);

        elinksApiJobScheduler.loadElinksJob();

        verify(dataloadSchedulerJobAudit, times(1)).auditSchedulerJobStatus(any(DataloadSchedulerJob.class));
        verify(elinksApiJobScheduler, times(1)).loadElinksData();
        verify(jdbcTemplate).update(any(String.class), eq(SUCCESS.getStatus()), eq(11));
    }

    private void assertValueProperty(String fieldName, String expectedValue) throws NoSuchFieldException {
        Field field = ElinksApiJobScheduler.class.getDeclaredField(fieldName);
        Value value = field.getAnnotation(Value.class);
        assertThat(value).isNotNull();
        assertThat(value.value()).isEqualTo(expectedValue);
    }

    private void setBaseUrl() {
        ReflectionTestUtils.setField(elinksApiJobScheduler, "eLinksWrapperBaseUrl", WRAPPER_BASE_URL);
    }

    private void enableAllStepFlags() {
        ReflectionTestUtils.setField(elinksApiJobScheduler, "isloadLocationEnabled", true);
        ReflectionTestUtils.setField(elinksApiJobScheduler, "isloadPersonEnabled", true);
        ReflectionTestUtils.setField(elinksApiJobScheduler, "isloadDeletedEnabled", true);
        ReflectionTestUtils.setField(elinksApiJobScheduler, "isloadLeaversEnabled", true);
        ReflectionTestUtils.setField(elinksApiJobScheduler, "isidamElasticSearchEnabled", true);
        ReflectionTestUtils.setField(elinksApiJobScheduler, "ispublishSidamIdToAsbEnabled", true);
        ReflectionTestUtils.setField(elinksApiJobScheduler, "isfetchIdamIdsEnabled", true);
    }
}
