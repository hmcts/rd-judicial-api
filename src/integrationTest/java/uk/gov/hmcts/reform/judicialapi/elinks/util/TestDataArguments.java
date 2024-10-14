package uk.gov.hmcts.reform.judicialapi.elinks.util;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record TestDataArguments(
        String eLinksPeopleApiResponseJson,
        String eLinksLocationApiResponseJson,
        String eLinksLeaversApiResponseJson,
        String eLinksDeletedApiResponseJson,
        int expectedAppointmentsSize,
        int expectedAuthorisationSize,
        int expectedRoleSize,
        boolean expectedActiveFlag,
        boolean expectedDeletedFlag,
        String expectedLastWorkingDate,
        String expectedDeletedOnDate,
        RefDataElinksConstants.JobStatus expectedJobStatus,
        int exceptionSize,
        String errorMsg1,
        String errorMsg2,
        HttpStatus httpStatus,
        String idamElasticSearchResponse,
        String expectedErrorMessage,
        int expectedUserProfiles,
        int expectedAuditRecords,
        boolean isAfterIdamElasticSearch,
        boolean isDuplicateUserProfile,
        String elasticSearchSidamId) {
}
