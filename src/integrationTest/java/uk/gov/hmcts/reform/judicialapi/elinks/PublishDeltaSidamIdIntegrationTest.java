package uk.gov.hmcts.reform.judicialapi.elinks;

import java.io.IOException;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAMSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PUBLISHSIDAM;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksDataLoadBaseTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus;

class PublishDeltaSidamIdIntegrationTest extends ElinksDataLoadBaseTest {

    @BeforeEach
    void beforeSetup() {
        deleteData();

        DataloadSchedulerJob dataloadSchedulerJob = new DataloadSchedulerJob();
        dataloadSchedulerJob.setJobStartTime(now());
        dataloadSchedulerJob.setPublishingStatus(JobStatus.IN_PROGRESS.getStatus());
        dataloadSchedulerJobRepository.save(dataloadSchedulerJob);

        ReflectionTestUtils.setField(publishSidamIdService, "elinkTopicPublisher", elinkTopicPublisher);
        given(idamTokenConfigProperties.getAuthorization()).willReturn(USER_PASSWORD);
    }


    @DisplayName("Should publish delta SidamId to topic")
    @Test
    void shouldPublishDeltaSidamIdToTopic() throws IOException {

        willDoNothing().given(elinkTopicPublisher).sendMessage(anyList(), anyString());

        final String peopleApiResponseJson = readJsonAsString(PEOPLE_NOT_EXPIRED_JSON);
        final String idamIdsSearchResponse = readJsonAsString(IDAM_IDS_SEARCH_RESPONSE_JSON);
        final String locationApiResponseJson = readJsonAsString(LOCATION_API_RESPONSE_JSON);

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, OK);
        stubIdamResponse(idamIdsSearchResponse, OK);
        stubIdamTokenResponse(OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);

        findSidamIdsByObjectIds(OK);
        publishSidamIds(OK);

        verifyAudit(SUCCESS);

        verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
    }


    private void verifyAudit(final JobStatus publishAuditStatus) {

        final List<ElinkDataSchedularAudit> eLinksDataSchedulerAudits =
                elinkSchedularAuditRepository.findAll()
                        .stream()
                        .sorted(comparing(ElinkDataSchedularAudit::getApiName))
                        .toList();
        assertThat(eLinksDataSchedulerAudits).isNotNull().isNotEmpty().hasSize(4);

        final ElinkDataSchedularAudit auditEntry1 = eLinksDataSchedulerAudits.get(0);
        final ElinkDataSchedularAudit auditEntry2 = eLinksDataSchedulerAudits.get(1);
        final ElinkDataSchedularAudit auditEntry3 = eLinksDataSchedulerAudits.get(2);
        final ElinkDataSchedularAudit auditEntry4 = eLinksDataSchedulerAudits.get(3);
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%"+auditEntry1.getApiName());
        assertThat(auditEntry1).isNotNull();
        assertThat(auditEntry2).isNotNull();
        assertThat(auditEntry3).isNotNull();
        assertThat(auditEntry4).isNotNull();

        assertThat(auditEntry1.getApiName()).isNotNull().isEqualTo(IDAMSEARCH);
        assertThat(auditEntry1.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry1.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry1.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry1.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry2.getApiName()).isNotNull().isEqualTo(LOCATIONAPI);
        assertThat(auditEntry2.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry2.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry2.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry2.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry3.getApiName()).isNotNull().isEqualTo(PEOPLEAPI);
        assertThat(auditEntry3.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry3.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry3.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry3.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry4.getApiName()).isNotNull().isEqualTo(PUBLISHSIDAM);
        assertThat(auditEntry4.getStatus()).isNotNull().isEqualTo(publishAuditStatus.getStatus());
        assertThat(auditEntry4.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry4.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry4.getSchedulerEndTime()).isNotNull();
    }

}