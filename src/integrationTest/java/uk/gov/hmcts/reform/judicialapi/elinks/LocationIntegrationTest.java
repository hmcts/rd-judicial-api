package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinksResponses;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksDataLoadBaseTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;
import uk.gov.hmcts.reform.judicialapi.elinks.util.TestDataArguments;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;

class LocationIntegrationTest extends ElinksDataLoadBaseTest {
    @BeforeEach
    void setUp() {
        deleteData();
    }

    @DisplayName("Success - ELinks Location Api Data Load Success Scenario")
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideDataForLocationApi")
    void shouldLoadLocationApiData(TestDataArguments testDataArguments) throws Exception {

        final String locationApiResponseJson = readJsonAsString(testDataArguments.eLinksLocationApiResponseJson());

        stubLocationApiResponse(locationApiResponseJson, OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);

        verifySavedOriginalELinksResponse();

        verifyLocationData();

        verifyLocationDataLoadAudit(testDataArguments.expectedJobStatus());
    }

    @DisplayName("Negative - ELinks Location Api Data Load Failure Scenarios")
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideDataLoadFailStatusCodes")
    void shouldFailToLoadLocationApiDataWhenELinksApiResponseNot200(TestDataArguments testDataArguments) {

        stubLocationApiResponse(null, testDataArguments.httpStatus());

        final HttpStatus expectedHttpStatus = testDataArguments.httpStatus() == SERVICE_UNAVAILABLE ? FORBIDDEN :
                testDataArguments.httpStatus();

        loadLocationData(expectedHttpStatus, RESPONSE_BODY_ERROR_MSG, testDataArguments.expectedErrorMessage());

        verifyLocationDataLoadAudit(testDataArguments.expectedJobStatus());
    }

    private void verifySavedOriginalELinksResponse() {

        final List<ElinksResponses> eLinksResponses = elinksResponsesRepository.findAll();

        assertThat(eLinksResponses).isNotNull().isNotEmpty().hasSize(1);

        final ElinksResponses locationElinksResponses = eLinksResponses.get(0);
        assertThat(locationElinksResponses).isNotNull();

        assertThat(locationElinksResponses.getApiName()).isNotNull().isEqualTo(LOCATIONAPI);
        assertThat(locationElinksResponses.getCreatedDate()).isNotNull();
        assertThat(locationElinksResponses.getElinksData()).isNotNull();
    }

    private void verifyLocationDataLoadAudit(RefDataElinksConstants.JobStatus expectedLocationLoadJobStatus) {

        final List<ElinkDataSchedularAudit> eLinksDataSchedulerAudits = elinkSchedularAuditRepository.findAll();
        assertThat(eLinksDataSchedulerAudits).isNotNull().isNotEmpty().hasSize(1);

        final ElinkDataSchedularAudit auditEntry = eLinksDataSchedulerAudits.get(0);
        assertThat(auditEntry).isNotNull();

        assertThat(auditEntry.getApiName()).isNotNull().isEqualTo(LOCATIONAPI);
        assertThat(auditEntry.getStatus()).isNotNull().isEqualTo(expectedLocationLoadJobStatus.getStatus());
        assertThat(auditEntry.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry.getSchedulerEndTime()).isNotNull();
    }

}
