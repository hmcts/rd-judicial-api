package uk.gov.hmcts.reform.judicialapi.elinks.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ForbiddenException;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkDeletedWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.SchedulerJobStatusResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ELinksService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinksPeopleService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.IdamElasticSearchService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.impl.ELinksServiceImpl;
import uk.gov.hmcts.reform.judicialapi.elinks.util.DataloadSchedulerJobAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataExceptionHelper;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataIngestionSchedularAudit;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.JobStatus.IN_PROGRESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.JobStatus.SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.FAILED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;

@ExtendWith(MockitoExtension.class)
class ElinksApiJobSchedulerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private DataloadSchedulerJobAudit dataloadSchedulerJobAudit;

    @Mock
    private ELinksService elinksService;

    @Mock
    private ElinksPeopleService elinksPeopleService;

    @Mock
    private IdamElasticSearchService idamElasticSearchService;

    @Mock
    private PublishSidamIdService publishSidamIdService;

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
        elinksApiJobScheduler.loadElinksData();

        verifyNoInteractions(elinksService, elinksPeopleService, idamElasticSearchService, publishSidamIdService);
        verify(elinksServiceImpl).cleanUpElinksResponses();
        verify(elinksServiceImpl).deleteJohProfiles(any());
    }

    @Test
    void shouldInvokeEveryEnabledSchedulerStep() {
        enableAllStepFlags();

        when(elinksService.retrieveLocation()).thenReturn(ResponseEntity.ok(new ElinkBaseLocationWrapperResponse()));
        when(elinksPeopleService.updatePeople()).thenReturn(ResponseEntity.ok(new ElinkPeopleWrapperResponse()));
        when(elinksService.retrieveLeavers()).thenReturn(ResponseEntity.ok(new ElinkLeaversWrapperResponse()));
        when(elinksService.retrieveDeleted()).thenReturn(ResponseEntity.ok(new ElinkDeletedWrapperResponse()));
        when(idamElasticSearchService.getIdamElasticSearchSyncFeed()).thenReturn(ResponseEntity.ok(new Object()));
        when(idamElasticSearchService.getIdamDetails()).thenReturn(ResponseEntity.ok(new Object()));
        when(publishSidamIdService.publishSidamIdToAsb())
            .thenReturn(ResponseEntity.ok(new SchedulerJobStatusResponse()));

        elinksApiJobScheduler.loadElinksData();

        verify(elinksService).retrieveLocation();
        verify(elinksPeopleService).updatePeople();
        verify(elinksService).retrieveLeavers();
        verify(elinksService).retrieveDeleted();
        verify(idamElasticSearchService).getIdamElasticSearchSyncFeed();
        verify(idamElasticSearchService).getIdamDetails();
        verify(publishSidamIdService).publishSidamIdToAsb();
        verify(elinksServiceImpl).cleanUpElinksResponses();
        verify(elinksServiceImpl).deleteJohProfiles(any());
    }

    @Test
    void shouldAuditFeatureFlagFailureForDirectLocationServiceCall() {
        ReflectionTestUtils.setField(elinksApiJobScheduler, "isloadLocationEnabled", true);
        String message = "jrd-elinks-location feature flag is not released";
        when(elinksService.retrieveLocation()).thenThrow(new ForbiddenException(message));

        elinksApiJobScheduler.loadElinksData();

        verify(elinkDataIngestionSchedularAudit).auditSchedulerStatus(
            eq(JUDICIAL_REF_DATA_ELINKS),
            any(),
            any(),
            eq(FAILED.getStatus()),
            eq(LOCATIONAPI),
            eq(message));
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
