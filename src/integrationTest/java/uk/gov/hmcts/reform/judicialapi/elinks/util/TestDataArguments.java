package uk.gov.hmcts.reform.judicialapi.elinks.util;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record TestDataArguments(
        String eLinksPeopleApiResponseJson,
        String eLinksPeopleApiUpdateResponseJson,
        String eLinksLocationApiResponseJson,
        String eLinksLeaversApiResponseJson,
        String eLinksDeletedApiResponseJson,
        int expectedAppointmentsSize,
        int expectedAuthorisationSize,
        int expectedRoleSize,
        int expectedAppointmentsSizeUpdate,
        int expectedAuthorisationSizeUpdate,
        int expectedRoleSizeUpdate,
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
        boolean isAfterIdamElasticSearch,
        boolean isDuplicateUserProfile,
        String elasticSearchSidamId) {
}
