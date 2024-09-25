package uk.gov.hmcts.reform.judicialapi.elinks.util;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readString;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DELETEDAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELASTICSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ACCESS_ERROR;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_FORBIDDEN;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_UNAUTHORIZED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAMSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.FAILED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.PARTIAL_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.OBJECTIDISDUPLICATED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PUBLISHSIDAM;

public class ElinksDataLoadBaseTest extends ELinksBaseIntegrationTest {

    protected static final String APPOINTMENT_BASE_LOCATION_MISSING_ERROR_MSG =
            "Appointment's Base Location ID : null is not available in location_type table";
    protected static final String APPOINTMENT_TYPE_MISSING_ERROR_MSG =
            "The Type field is null for the given Appointment.";
    protected static final String APPOINTMENT_ID_UNAVAILABLE_ERROR_MSG =
            "Appointment ID : 25513 is not available in Appointment Table";
    protected static final String INVALID_JUDICIARY_ROLE = "Invalid Judiciary Role";
    protected static final String INVALID_BASE_LOCATION_ID_ERROR_MSG =
            "Appointment's Base Location ID : 5577 is not available in location_type table";
    protected static final String INVALID_APPOINTMENT_LOCATION_ERROR_MSG =
            "Location : North London  is not available in jrd_lrd_region_mapping table";
    protected static final String INVALID_APPOINTMENT_ROLE_NAME_ERROR_MSG = "Role Name : Unknown is invalid";
    protected static final String BASE_LOCATION_PARENT_ID_NULL_ERROR_MSG =
            "The Parent ID is null/blanks for Tribunal Base Location ID 1122 in the Location_Type table.";
    private static final String WIREMOCK_RESPONSES_FOLDER = "/wiremock_responses";
    protected static final String LOCATION_API_RESPONSE_JSON = WIREMOCK_RESPONSES_FOLDER + "/location.json";
    protected static final String LEAVERS_API_RESPONSE_JSON = WIREMOCK_RESPONSES_FOLDER + "/leavers.json";
    protected static final String DELETED_API_RESPONSE_JSON = WIREMOCK_RESPONSES_FOLDER + "/deleted.json";
    protected static final String PEOPLE_API_RESPONSE_JSON = WIREMOCK_RESPONSES_FOLDER + "/people.json";
    protected static final String PEOPLE_LOAD_DELETE_API_RESPONSE_JSON =
            WIREMOCK_RESPONSES_FOLDER + "/people_load_delete.json";
    protected static final String PEOPLE_API_NULL_APPOINTMENT_ID_RESPONSE_JSON =
            WIREMOCK_RESPONSES_FOLDER + "/people_null_appointment_id_for_authorisation.json";

    protected static final String PEOPLE_API_DUPLICATE_OBJECT_ID_RESPONSE_JSON =
            WIREMOCK_RESPONSES_FOLDER + "/people_duplicate_object_Id.json";
    protected static final String PEOPLE_API_DUPLICATE_PERSONAL_CODE_RESPONSE_JSON =
            WIREMOCK_RESPONSES_FOLDER + "/people_duplicate_personal_code.json";
    protected static final String IDAM_IDS_SEARCH_RESPONSE_JSON =
            WIREMOCK_RESPONSES_FOLDER + "/sidamId_update_for_matched_objectId.json";
    protected static final String LOCATION_API_RESPONSE_WITH_PARENT_ID_NULL_JSON =
            WIREMOCK_RESPONSES_FOLDER + "/location_with_null_parent_id.json";
    protected static final String PEOPLE_API_BASE_LOCATION_PARENT_ID_NULL_JSON =
            WIREMOCK_RESPONSES_FOLDER + "/people_with_base_location_parent_id_null.json";
    protected static final String PEOPLE_MISSING_JUDICIARY_ROLE_NAME_JSON =
            WIREMOCK_RESPONSES_FOLDER + "/people_missing_judiciary_role_name_id.json";
    protected static final String PEOPLE_MISSING_BASE_LOCATION_ID_JSON =
            WIREMOCK_RESPONSES_FOLDER + "/people_missing_base_location_id.json";
    protected static final String PEOPLE_MISSING_APPOINTMENT_TYPE_JSON =
            WIREMOCK_RESPONSES_FOLDER + "/people_missing_appointment_type.json";
    protected static final String PEOPLE_INVALID_BASE_LOCATION_ID_JSON =
            WIREMOCK_RESPONSES_FOLDER + "/people_invalid_base_location_id.json";
    protected static final String PEOPLE_INVALID_APPOINTMENT_LOCATION_JSON =
            WIREMOCK_RESPONSES_FOLDER + "/people_invalid_appointment_location.json";
    protected static final String PEOPLE_INVALID_APPOINTMENT_ROLE_NAME_JSON =
            WIREMOCK_RESPONSES_FOLDER + "/people_invalid_appointment_role_name.json";
    private static final String YYYY_MM_DD_T_HH_MM_SS_SSS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static Stream<Arguments> provideDataForPeopleApi() {

        final TestDataArguments successLoadTestDataArguments =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_API_RESPONSE_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .expectedAppointmentsSize(4)
                        .expectedAuthorisationSize(4)
                        .expectedRoleSize(2)
                        .expectedUserProfiles(2)
                        .expectedJobStatus(SUCCESS)
                        .expectedActiveFlag(true)
                        .expectedAuditRecords(2)
                        .expectedLastWorkingDate("2028-07-23")
                        .build();

        final TestDataArguments successLoadAndDeleteTestDataArguments =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_LOAD_DELETE_API_RESPONSE_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .expectedAppointmentsSize(2)
                        .expectedAuthorisationSize(2)
                        .expectedDeletedOnDate(LocalDate.now().toString())
                        .expectedDeletedFlag(true)
                        .expectedRoleSize(1)
                        .expectedUserProfiles(2)
                        .expectedAuditRecords(2)
                        .expectedAuditRecords(3)
                        .expectedJobStatus(SUCCESS)
                        .expectedActiveFlag(true)
                        .expectedLastWorkingDate("2028-07-23")
                        .build();

        final TestDataArguments roleMissingTestDataArguments =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_MISSING_JUDICIARY_ROLE_NAME_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .expectedAppointmentsSize(4)
                        .expectedAuthorisationSize(4)
                        .expectedRoleSize(1)
                        .expectedUserProfiles(2)
                        .expectedAuditRecords(2)
                        .expectedJobStatus(PARTIAL_SUCCESS)
                        .exceptionSize(1)
                        .expectedActiveFlag(true)
                        .expectedLastWorkingDate("2028-07-23")
                        .errorMsg1(INVALID_JUDICIARY_ROLE)
                        .build();

        final TestDataArguments baseLocationMissingTestDataArguments =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_MISSING_BASE_LOCATION_ID_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .expectedAppointmentsSize(3)
                        .expectedAuthorisationSize(3)
                        .expectedRoleSize(2)
                        .expectedUserProfiles(2)
                        .expectedAuditRecords(2)
                        .expectedActiveFlag(true)
                        .expectedLastWorkingDate("2028-07-23")
                        .expectedJobStatus(PARTIAL_SUCCESS)
                        .exceptionSize(2)
                        .errorMsg1(APPOINTMENT_BASE_LOCATION_MISSING_ERROR_MSG)
                        .errorMsg2(APPOINTMENT_ID_UNAVAILABLE_ERROR_MSG)
                        .build();

        final TestDataArguments appointmentTypeMissingTestDataArguments =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_MISSING_APPOINTMENT_TYPE_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .expectedAppointmentsSize(3)
                        .expectedAuthorisationSize(3)
                        .expectedRoleSize(2)
                        .expectedUserProfiles(2)
                        .expectedAuditRecords(2)
                        .expectedActiveFlag(true)
                        .expectedLastWorkingDate("2028-07-23")
                        .expectedJobStatus(PARTIAL_SUCCESS)
                        .exceptionSize(2)
                        .errorMsg1(APPOINTMENT_TYPE_MISSING_ERROR_MSG)
                        .errorMsg2(APPOINTMENT_ID_UNAVAILABLE_ERROR_MSG)
                        .build();

        final TestDataArguments invalidBaseLocationIdTestDataArguments =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_INVALID_BASE_LOCATION_ID_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .expectedAppointmentsSize(3)
                        .expectedAuthorisationSize(3)
                        .expectedRoleSize(2)
                        .expectedUserProfiles(2)
                        .expectedAuditRecords(2)
                        .expectedActiveFlag(true)
                        .expectedLastWorkingDate("2028-07-23")
                        .expectedJobStatus(PARTIAL_SUCCESS)
                        .exceptionSize(2)
                        .errorMsg1(INVALID_BASE_LOCATION_ID_ERROR_MSG)
                        .errorMsg2(APPOINTMENT_ID_UNAVAILABLE_ERROR_MSG)
                        .build();

        final TestDataArguments invalidAppointmentLocationTestDataArguments =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_INVALID_APPOINTMENT_LOCATION_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .expectedAppointmentsSize(3)
                        .expectedAuthorisationSize(3)
                        .expectedRoleSize(2)
                        .expectedUserProfiles(2)
                        .expectedAuditRecords(2)
                        .expectedActiveFlag(true)
                        .expectedLastWorkingDate("2028-07-23")
                        .expectedJobStatus(PARTIAL_SUCCESS)
                        .exceptionSize(2)
                        .errorMsg1(INVALID_APPOINTMENT_LOCATION_ERROR_MSG)
                        .errorMsg2(APPOINTMENT_ID_UNAVAILABLE_ERROR_MSG)
                        .build();

        final TestDataArguments invalidAppointmentRoleNameTestDataArguments =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_INVALID_APPOINTMENT_ROLE_NAME_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .expectedAppointmentsSize(3)
                        .expectedAuthorisationSize(3)
                        .expectedRoleSize(2)
                        .expectedUserProfiles(2)
                        .expectedAuditRecords(2)
                        .expectedActiveFlag(true)
                        .expectedLastWorkingDate("2028-07-23")
                        .expectedJobStatus(PARTIAL_SUCCESS)
                        .exceptionSize(2)
                        .errorMsg1(INVALID_APPOINTMENT_ROLE_NAME_ERROR_MSG)
                        .errorMsg2(APPOINTMENT_ID_UNAVAILABLE_ERROR_MSG)
                        .build();

        final TestDataArguments locationParentIdNullTestDataArguments =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_API_BASE_LOCATION_PARENT_ID_NULL_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_WITH_PARENT_ID_NULL_JSON)
                        .expectedAppointmentsSize(3)
                        .expectedAuthorisationSize(3)
                        .expectedRoleSize(2)
                        .expectedUserProfiles(2)
                        .expectedAuditRecords(2)
                        .expectedActiveFlag(true)
                        .expectedLastWorkingDate("2028-07-23")
                        .expectedJobStatus(PARTIAL_SUCCESS)
                        .exceptionSize(2)
                        .errorMsg1(BASE_LOCATION_PARENT_ID_NULL_ERROR_MSG)
                        .errorMsg2(APPOINTMENT_ID_UNAVAILABLE_ERROR_MSG)
                        .build();

        final TestDataArguments duplicateObjectIdTestDataArguments =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_API_DUPLICATE_OBJECT_ID_RESPONSE_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .isDuplicateUserProfile(true)
                        .expectedAppointmentsSize(2)
                        .expectedAuthorisationSize(2)
                        .expectedRoleSize(1)
                        .expectedUserProfiles(1)
                        .expectedAuditRecords(2)
                        .expectedJobStatus(PARTIAL_SUCCESS)
                        .exceptionSize(1)
                        .expectedActiveFlag(true)
                        .expectedLastWorkingDate("2028-07-23")
                        .errorMsg1(String.format(OBJECTIDISDUPLICATED, "5f8b26ba-0c8b-4192-b5c7-311d737f0cae"))
                        .build();

        final TestDataArguments duplicatePersonalCodeTestDataArguments =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_API_DUPLICATE_PERSONAL_CODE_RESPONSE_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .isDuplicateUserProfile(true)
                        .expectedAppointmentsSize(2)
                        .expectedAuthorisationSize(2)
                        .expectedRoleSize(1)
                        .expectedUserProfiles(1)
                        .expectedAuditRecords(2)
                        .expectedJobStatus(PARTIAL_SUCCESS)
                        .exceptionSize(1)
                        .expectedActiveFlag(true)
                        .expectedLastWorkingDate("2028-07-23")
                        .errorMsg1("Personal Code : 4913085 is already loaded")
                        .build();

        return Stream.of(
                arguments(
                        named("Should load people data with success status", successLoadTestDataArguments)),
                arguments(
                        named("Should Load and Delete Success Scenarios", successLoadAndDeleteTestDataArguments)),
                arguments(
                        named(
                                "Should load people data with partial success status when judiciary role name "
                                        + "id is missing for a user", roleMissingTestDataArguments)),
                arguments(
                        named(
                                "Should load people data with partial success status when appointment base "
                                        + "location id is missing for a user", baseLocationMissingTestDataArguments)),
                arguments(
                        named(
                                "Should load people data with partial success status when appointment type is "
                                        + "missing for a user", appointmentTypeMissingTestDataArguments)),
                arguments(
                        named(
                                "Should load people data with partial success status when base location id "
                                        + "is invalid", invalidBaseLocationIdTestDataArguments)),
                arguments(
                        named(
                                "Should load people data with partial success status when appointment location "
                                        + "is invalid", invalidAppointmentLocationTestDataArguments)),
                arguments(
                        named(
                                "Should load people data with partial success status when appointment role name "
                                        + "is invalid", invalidAppointmentRoleNameTestDataArguments)),
                arguments(
                        named(
                                "Should load people data with partial success status when location parent id is "
                                        + "null", locationParentIdNullTestDataArguments)),
                arguments(
                        named("Should load people data with partial success status when duplicate object "
                                + "is present", duplicateObjectIdTestDataArguments)),

                arguments(
                        named("Should load people data with partial success status when duplicate personal code "
                                + "is present", duplicatePersonalCodeTestDataArguments))


        );
    }

    public static Stream<Arguments> provideDataForPeopleLoadAndDeleteApi() {

        final TestDataArguments successLoadAndDeleteTestDataArguments =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_LOAD_DELETE_API_RESPONSE_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .expectedAppointmentsSize(2)
                        .expectedDeletedOnDate(LocalDate.now().toString())
                        .expectedDeletedFlag(true)
                        .expectedAuthorisationSize(2)
                        .expectedRoleSize(1)
                        .expectedUserProfiles(2)
                        .expectedJobStatus(SUCCESS)
                        .expectedActiveFlag(true)
                        .expectedLastWorkingDate("2028-07-23")
                        .build();

        return Stream.of(
                arguments(
                        named("Should Load and Delete Success Scenarios", successLoadAndDeleteTestDataArguments))
        );
    }

    protected void loadLocationData(final HttpStatus expectedHttpStatus,
                                    final String messageKey,
                                    final String expectedMessage) {
        final ValidatableResponse locationLoadResponse = elinksReferenceDataClient.loadLocationData();
        locationLoadResponse
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .body(messageKey, equalTo(expectedMessage));
    }

    protected void loadPeopleData(final HttpStatus expectedHttpStatus,
                                  final String messageKey,
                                  final String expectedMessage) {
        final ValidatableResponse validatableResponse = elinksReferenceDataClient.loadPeopleData();
        validatableResponse
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .body(messageKey, equalTo(expectedMessage));
    }

    protected void elasticSearchLoadSidamIdsByObjectIds(final HttpStatus expectedHttpStatus) {
        final ValidatableResponse validatableResponse =
                elinksReferenceDataClient.elasticSearchLoadSidamIdsByObjectIds();
        validatableResponse
                .assertThat()
                .statusCode(expectedHttpStatus.value());
    }

    protected void findSidamIdsByObjectIds(final HttpStatus expectedHttpStatus) {
        final ValidatableResponse validatableResponse = elinksReferenceDataClient.findSidamIdsByObjectIds();
        validatableResponse
                .assertThat()
                .statusCode(expectedHttpStatus.value());
    }

    protected void publishSidamIds(final HttpStatus expectedHttpStatus) {
        final ValidatableResponse validatableResponse = elinksReferenceDataClient.publishSidamIds();
        validatableResponse
                .assertThat()
                .statusCode(expectedHttpStatus.value());
    }

    protected void loadLeaversData(final HttpStatus expectedHttpStatus,
                                   final String messageKey,
                                   final String expectedMessage) {
        final ValidatableResponse validatableResponse = elinksReferenceDataClient.loadLeaversData();
        validatableResponse
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .body(messageKey, equalTo(expectedMessage));
    }

    protected void loadDeletedData(final HttpStatus expectedHttpStatus,
                                   final String messageKey,
                                   final String expectedMessage) {
        final ValidatableResponse validatableResponse = elinksReferenceDataClient.loadDeletedData();
        validatableResponse
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .body(messageKey, equalTo(expectedMessage));
    }

    protected String readJsonAsString(String jsonFilePath) throws IOException {
        return readString(Paths.get(requireNonNull(this.getClass().getResource(jsonFilePath)).getPath()), UTF_8);

    }

    protected Stream<Arguments> provideDataLoadFailStatusCodes() {
        final TestDataArguments badRequest =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_API_RESPONSE_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .httpStatus(BAD_REQUEST)
                        .expectedJobStatus(FAILED)
                        .expectedAuditRecords(3)
                        .expectedErrorMessage(ELINKS_ERROR_RESPONSE_BAD_REQUEST)
                        .build();

        final TestDataArguments unAuthorised =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_API_RESPONSE_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .httpStatus(UNAUTHORIZED)
                        .expectedJobStatus(FAILED)
                        .expectedAuditRecords(3)
                        .expectedErrorMessage(ELINKS_ERROR_RESPONSE_UNAUTHORIZED)
                        .build();

        final TestDataArguments forbidden =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_API_RESPONSE_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .httpStatus(FORBIDDEN)
                        .expectedJobStatus(FAILED)
                        .expectedAuditRecords(3)
                        .expectedErrorMessage(ELINKS_ERROR_RESPONSE_FORBIDDEN)
                        .build();

        final TestDataArguments notFound =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_API_RESPONSE_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .httpStatus(NOT_FOUND)
                        .expectedJobStatus(FAILED)
                        .expectedAuditRecords(3)
                        .expectedErrorMessage(ELINKS_ERROR_RESPONSE_NOT_FOUND)
                        .build();

        final TestDataArguments tooManyRequests =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_API_RESPONSE_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .httpStatus(TOO_MANY_REQUESTS)
                        .expectedJobStatus(FAILED)
                        .expectedAuditRecords(3)
                        .expectedErrorMessage(ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS)
                        .build();

        final TestDataArguments serviceUnavailable =
                TestDataArguments
                        .builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_API_RESPONSE_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .httpStatus(SERVICE_UNAVAILABLE)
                        .expectedJobStatus(FAILED)
                        .expectedAuditRecords(3)
                        .expectedErrorMessage(ELINKS_ACCESS_ERROR)
                        .build();

        return Stream.of(
                arguments(named(BAD_REQUEST.name(), badRequest)),
                arguments(named(UNAUTHORIZED.name(), unAuthorised)),
                arguments(named(FORBIDDEN.name(), forbidden)),
                arguments(named(NOT_FOUND.name(), notFound)),
                arguments(named(TOO_MANY_REQUESTS.name(), tooManyRequests)),
                arguments(named(SERVICE_UNAVAILABLE.name(), serviceUnavailable))
        );

    }

    protected Stream<Arguments> provideDataForLeaversApi() {
        final TestDataArguments successLoadTestDataArguments =
                TestDataArguments.builder()
                        .eLinksPeopleApiResponseJson(PEOPLE_API_RESPONSE_JSON)
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .eLinksLeaversApiResponseJson(LEAVERS_API_RESPONSE_JSON)
                        .expectedAppointmentsSize(4)
                        .expectedAuthorisationSize(4)
                        .expectedRoleSize(2)
                        .expectedUserProfiles(2)
                        .expectedActiveFlag(false)
                        .expectedLastWorkingDate("2023-03-01")
                        .expectedJobStatus(SUCCESS)
                        .build();

        return Stream.of(
                arguments(
                        named("Should load leavers data with success status", successLoadTestDataArguments)));
    }

    protected Stream<Arguments> provideDataForDeletedApi() {

        final TestDataArguments successLoadTestDataArguments =
                TestDataArguments.builder()
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .eLinksPeopleApiResponseJson(PEOPLE_API_RESPONSE_JSON)
                        .eLinksDeletedApiResponseJson(DELETED_API_RESPONSE_JSON)
                        .expectedAppointmentsSize(4)
                        .expectedAuthorisationSize(4)
                        .expectedRoleSize(2)
                        .expectedUserProfiles(2)
                        .expectedAuditRecords(3)
                        .expectedAuditRecords(4)
                        .expectedActiveFlag(false)
                        .expectedDeletedFlag(true)
                        .expectedDeletedOnDate("2022-07-10")
                        .expectedLastWorkingDate("2028-07-23")
                        .expectedJobStatus(SUCCESS)
                        .build();

        return Stream.of(
                arguments(
                        named("Should load deleted data with success status", successLoadTestDataArguments)));
    }

    protected Stream<Arguments> provideDataForLocationApi() {
        final TestDataArguments successLoadTestDataArguments =
                TestDataArguments.builder()
                        .eLinksLocationApiResponseJson(LOCATION_API_RESPONSE_JSON)
                        .expectedJobStatus(SUCCESS)
                        .build();

        return Stream.of(
                arguments(
                        named("Should load location data with success status", successLoadTestDataArguments)));
    }

    protected void verifyUserProfileData(TestDataArguments testDataArguments) {
        final List<UserProfile> userprofile =
                profileRepository.findAll().stream()
                        .sorted(Comparator.comparing(UserProfile::getPersonalCode))
                        .toList();
        assertThat(userprofile).isNotNull().isNotEmpty().hasSize(testDataArguments.expectedUserProfiles());

        final UserProfile firstUserData = userprofile.get(0);
        assertThat(firstUserData).isNotNull();
        verifyFirstUserData(firstUserData, testDataArguments);

        if (testDataArguments.expectedUserProfiles() > 1) {
            final UserProfile secondUserData = userprofile.get(1);
            assertThat(secondUserData).isNotNull();
            verifySecondUserData(secondUserData, testDataArguments);
        }
    }

    private void verifyFirstUserData(UserProfile firstUser, TestDataArguments testDataArguments) {
        assertThat(firstUser.getObjectId()).isEqualTo("5f8b26ba-0c8b-4192-b5c7-311d737f0cae");
        assertThat(firstUser.getPersonalCode()).isEqualTo("4913085");
        assertThat(firstUser.getKnownAs()).isEqualTo("Rachel");
        assertThat(firstUser.getSurname()).isEqualTo("Jones");
        assertThat(firstUser.getFullName()).isEqualTo("District Judge Rachel Jones");
        assertThat(firstUser.getInitials()).isEqualTo("RJ");
        assertThat(firstUser.getPostNominals()).isNull();
        assertThat(firstUser.getEmailId()).isEqualTo("DJ.Rachel.Jones@ejudiciary.net");
        assertThat(firstUser.getActiveFlag()).isEqualTo(true);
        assertThat(firstUser.getCreatedDate()).isNotNull();
        if (testDataArguments.isAfterIdamElasticSearch()) {
            assertThat(firstUser.getSidamId()).isNotNull().isEqualTo(testDataArguments.elasticSearchSidamId());
        } else {
            assertThat(firstUser.getSidamId()).isNull();
        }

        assertThat(firstUser.getRetirementDate()).isEqualTo("2026-07-23");
        assertThat(firstUser.getLastWorkingDate()).isEqualTo("2026-07-23");
        assertThat(firstUser.getDeletedOn()).isNull();
        assertThat(firstUser.getDeletedFlag()).isNull();

    }

    private void verifySecondUserData(UserProfile secondUser,
                                      TestDataArguments testDataArguments) {
        assertThat(secondUser.getObjectId()).isEqualTo("8eft26ba-0c8b-4192-b5c7-311d737f0cae");
        assertThat(secondUser.getPersonalCode()).isEqualTo("4913089");
        assertThat(secondUser.getKnownAs()).isEqualTo("Mathew");
        assertThat(secondUser.getSurname()).isEqualTo("gomes");
        assertThat(secondUser.getFullName()).isEqualTo("District Judge Mathew gomes");
        assertThat(secondUser.getInitials()).isEqualTo("PJ");
        assertThat(secondUser.getPostNominals()).isNull();
        assertThat(secondUser.getEmailId()).isEqualTo("DJ.Mathew.Gomes@ejudiciary.net");
        assertThat(secondUser.getActiveFlag()).isEqualTo(testDataArguments.expectedActiveFlag());
        assertThat(secondUser.getCreatedDate()).isNotNull();
        if (nonNull(testDataArguments.expectedDeletedOnDate())) {
            assertThat(secondUser.getDeletedOn().toLocalDate().toString())
                    .isEqualTo(testDataArguments.expectedDeletedOnDate());
            assertThat(secondUser.getDeletedFlag()).isEqualTo(testDataArguments.expectedDeletedFlag());
        } else {
            assertThat(secondUser.getDeletedOn()).isNull();
            assertThat(secondUser.getDeletedFlag()).isNull();
        }
        assertThat(secondUser.getSidamId()).isNull();
        assertThat(secondUser.getRetirementDate()).isEqualTo("2028-07-23");
        assertThat(secondUser.getLastWorkingDate()).isEqualTo(testDataArguments.expectedLastWorkingDate());
    }

    protected void verifyUserAppointmentsData(TestDataArguments testDataArguments) {
        final List<Appointment> appointments = appointmentsRepository.findAll();

        assertThat(appointments).isNotNull().isNotEmpty().hasSize(testDataArguments.expectedAppointmentsSize());

        verifyFirstUserAppointmentsData(appointments);

        if (!testDataArguments.isDuplicateUserProfile() && testDataArguments.expectedUserProfiles() > 1) {
            verifySecondUserAppointmentsData(appointments, testDataArguments.expectedAppointmentsSize());
        }
    }

    private void verifyFirstUserAppointmentsData(List<Appointment> appointments) {
        Appointment firstUserAppointments1 = appointments.get(0);
        Appointment firstUserAppointments2 = appointments.get(1);

        assertThat(firstUserAppointments1).isNotNull();
        assertThat(firstUserAppointments2).isNotNull();

        assertThat(firstUserAppointments1.getPersonalCode()).isEqualTo("4913085");
        assertThat(firstUserAppointments1.getAppointmentId()).isEqualTo("25502");
        assertThat(firstUserAppointments1.getRoleNameId()).isEqualTo("45");
        assertThat(firstUserAppointments1.getType()).isEqualTo("Courts");
        assertThat(firstUserAppointments1.getBaseLocationId()).isEqualTo("768");
        assertThat(firstUserAppointments1.getEpimmsId()).isEqualTo("1126");
        assertThat(firstUserAppointments1.getIsPrincipleAppointment()).isTrue();
        assertThat(firstUserAppointments1.getStartDate()).isEqualTo("2010-03-23");
        assertThat(firstUserAppointments1.getEndDate()).isEqualTo("2026-07-23");
        assertThat(firstUserAppointments1.getContractTypeId()).isEqualTo("5");
        assertThat(firstUserAppointments1.getAppointmentMapping()).isEqualTo("District Judge");
        assertThat(firstUserAppointments1.getAppointmentType()).isEqualTo("SPTW-50%");
        assertThat(firstUserAppointments1.getCreatedDate()).isNotNull();
        assertThat(firstUserAppointments1.getLastLoadedDate()).isNotNull();
        assertThat(firstUserAppointments1.getLocation()).isEqualTo("London");
        assertThat(firstUserAppointments1.getJoBaseLocationId()).isEqualTo("768");

        assertThat(firstUserAppointments2.getPersonalCode()).isEqualTo("4913085");
        assertThat(firstUserAppointments2.getAppointmentId()).isEqualTo("25503");
        assertThat(firstUserAppointments2.getRoleNameId()).isEqualTo("84");
        assertThat(firstUserAppointments2.getType()).isEqualTo("Tribunals");
        assertThat(firstUserAppointments2.getBaseLocationId()).isEqualTo("1815");
        assertThat(firstUserAppointments2.getEpimmsId()).isEqualTo("1123");
        assertThat(firstUserAppointments2.getIsPrincipleAppointment()).isFalse();
        assertThat(firstUserAppointments2.getStartDate()).isEqualTo("2007-05-11");
        assertThat(firstUserAppointments2.getEndDate()).isEqualTo("2014-06-20");
        assertThat(firstUserAppointments2.getContractTypeId()).isEqualTo("1");
        assertThat(firstUserAppointments2.getAppointmentMapping()).isEqualTo("Tribunal Judge");
        assertThat(firstUserAppointments2.getAppointmentType()).isEqualTo("Fee-paid");
        assertThat(firstUserAppointments2.getCreatedDate()).isNotNull();
        assertThat(firstUserAppointments2.getLastLoadedDate()).isNotNull();
        assertThat(firstUserAppointments2.getLocation()).isEqualTo("Unassigned");
        assertThat(firstUserAppointments2.getJoBaseLocationId()).isEqualTo("2218");
    }

    private void verifySecondUserAppointmentsData(List<Appointment> appointments, int size) {
        if (isSecondUserDeleted()) {
            return;
        }
        Appointment secondUserAppointments1 = appointments.get(2);
        assertThat(secondUserAppointments1).isNotNull();

        assertThat(secondUserAppointments1.getPersonalCode()).isEqualTo("4913089");
        assertThat(secondUserAppointments1.getAppointmentId()).isEqualTo("25512");
        assertThat(secondUserAppointments1.getRoleNameId()).isEqualTo("45");
        assertThat(secondUserAppointments1.getType()).isEqualTo("Courts");
        assertThat(secondUserAppointments1.getBaseLocationId()).isEqualTo("768");
        assertThat(secondUserAppointments1.getIsPrincipleAppointment()).isTrue();
        assertThat(secondUserAppointments1.getStartDate()).isEqualTo("2010-03-23");
        assertThat(secondUserAppointments1.getEndDate()).isEqualTo("2026-07-23");
        assertThat(secondUserAppointments1.getContractTypeId()).isEqualTo("5");
        assertThat(secondUserAppointments1.getAppointmentMapping()).isEqualTo("District Judge");
        assertThat(secondUserAppointments1.getAppointmentType()).isEqualTo("SPTW-50%");
        assertThat(secondUserAppointments1.getCreatedDate()).isNotNull();
        assertThat(secondUserAppointments1.getLastLoadedDate()).isNotNull();
        assertThat(secondUserAppointments1.getLocation()).isEqualTo("London");
        assertThat(secondUserAppointments1.getJoBaseLocationId()).isEqualTo("768");

        if (size == 4) {
            Appointment secondUserAppointments2 = appointments.get(3);
            assertThat(secondUserAppointments2).isNotNull();

            assertThat(secondUserAppointments2.getPersonalCode()).isEqualTo("4913089");
            assertThat(secondUserAppointments2.getAppointmentId()).isEqualTo("25513");
            assertThat(secondUserAppointments2.getRoleNameId()).isEqualTo("84");
            assertThat(secondUserAppointments2.getType()).isEqualTo("Tribunals");
            assertThat(secondUserAppointments2.getBaseLocationId()).isEqualTo("1815");
            assertThat(secondUserAppointments2.getIsPrincipleAppointment()).isFalse();
            assertThat(secondUserAppointments2.getStartDate()).isEqualTo("2007-05-11");
            assertThat(secondUserAppointments2.getEndDate()).isEqualTo("2014-06-20");
            assertThat(secondUserAppointments2.getContractTypeId()).isEqualTo("1");
            assertThat(secondUserAppointments2.getAppointmentMapping()).isEqualTo("Tribunal Judge");
            assertThat(secondUserAppointments2.getAppointmentType()).isEqualTo("Fee-paid");
            assertThat(secondUserAppointments2.getCreatedDate()).isNotNull();
            assertThat(secondUserAppointments2.getLastLoadedDate()).isNotNull();
            assertThat(secondUserAppointments2.getLocation()).isEqualTo("Unassigned");
            assertThat(secondUserAppointments2.getJoBaseLocationId()).isEqualTo("2218");
        }
    }

    private boolean isSecondUserDeleted() {
        final List<UserProfile> userprofile =
                profileRepository.findAll().stream()
                        .sorted(Comparator.comparing(UserProfile::getPersonalCode))
                        .toList();
        UserProfile secondUser = userprofile.get(1);
        Boolean deletedFlag = secondUser.getDeletedFlag();
        return deletedFlag != null && deletedFlag;
    }

    protected void verifyUserAuthorisationsData(TestDataArguments testDataArguments) {
        verifyUserAuthorisationsData(testDataArguments, false);
    }

    protected void verifyUserAuthorisationsData(TestDataArguments testDataArguments, boolean isAppointmentIdNull) {
        final List<Authorisation> authorisations = authorisationsRepository.findAll();

        assertThat(authorisations).isNotNull().isNotEmpty().hasSize(testDataArguments.expectedAuthorisationSize());

        verifyFirstUserAuthorisationsData(authorisations, isAppointmentIdNull);
        if (!testDataArguments.isDuplicateUserProfile() && testDataArguments.expectedUserProfiles() > 1) {
            verifySecondUserAuthorisationsData(authorisations, testDataArguments.expectedAuthorisationSize());
        }
    }

    private void verifyFirstUserAuthorisationsData(List<Authorisation> authorisations, boolean isAppointmentIdNull) {
        final Authorisation firstUserAuthorisation1 = authorisations.get(0);
        final Authorisation firstUserAuthorisation2 = authorisations.get(1);

        assertThat(firstUserAuthorisation1).isNotNull();
        assertThat(firstUserAuthorisation2).isNotNull();

        assertThat(firstUserAuthorisation1.getPersonalCode()).isEqualTo("4913085");
        assertThat(firstUserAuthorisation1.getJurisdiction()).isEqualTo("Family");
        assertThat(firstUserAuthorisation1.getStartDate()).isEqualTo("2017-03-24");
        assertThat(firstUserAuthorisation1.getEndDate()).isEqualTo("2022-07-23");
        assertThat(firstUserAuthorisation1.getCreatedDate()).isNotNull();
        assertThat(firstUserAuthorisation1.getLastUpdated()).isNotNull();
        assertThat(firstUserAuthorisation1.getTicketCode()).isEqualTo("313");
        assertThat(firstUserAuthorisation1.getLowerLevel()).isEqualTo("Court of Protection");
        assertThat(firstUserAuthorisation1.getAppointmentId()).isEqualTo("25502");
        assertThat(firstUserAuthorisation1.getAuthorisationId()).isEqualTo("9918");
        assertThat(firstUserAuthorisation1.getJurisdictionId()).isEqualTo("26");

        assertThat(firstUserAuthorisation2.getPersonalCode()).isEqualTo("4913085");
        assertThat(firstUserAuthorisation2.getJurisdiction()).isEqualTo("Tribunals");
        assertThat(firstUserAuthorisation2.getStartDate()).isEqualTo("2007-05-11");
        assertThat(firstUserAuthorisation2.getEndDate()).isEqualTo("2014-06-20");
        assertThat(firstUserAuthorisation2.getCreatedDate()).isNotNull();
        assertThat(firstUserAuthorisation2.getLastUpdated()).isNotNull();
        assertThat(firstUserAuthorisation2.getTicketCode()).isEqualTo("328");
        assertThat(firstUserAuthorisation2.getLowerLevel())
                .isEqualTo("Criminal Injuries Compensations");
        if (isAppointmentIdNull) {
            assertNull(firstUserAuthorisation2.getAppointmentId());
        } else {
            assertThat(firstUserAuthorisation2.getAppointmentId()).isEqualTo("25503");
        }
        assertThat(firstUserAuthorisation2.getAuthorisationId()).isEqualTo("14752");
        assertThat(firstUserAuthorisation2.getJurisdictionId()).isEqualTo("27");
    }

    private void verifySecondUserAuthorisationsData(List<Authorisation> authorisations, int size) {
        if (isSecondUserDeleted()) {
            return;
        }
        Authorisation secondUserAuthorisation1 = authorisations.get(2);
        assertThat(secondUserAuthorisation1).isNotNull();

        assertThat(secondUserAuthorisation1.getPersonalCode()).isEqualTo("4913089");
        assertThat(secondUserAuthorisation1.getJurisdiction()).isEqualTo("Family");
        assertThat(secondUserAuthorisation1.getStartDate()).isEqualTo("2017-03-24");
        assertThat(secondUserAuthorisation1.getEndDate()).isEqualTo("2022-07-23");
        assertThat(secondUserAuthorisation1.getCreatedDate()).isNotNull();
        assertThat(secondUserAuthorisation1.getLastUpdated()).isNotNull();
        assertThat(secondUserAuthorisation1.getTicketCode()).isEqualTo("313");
        assertThat(secondUserAuthorisation1.getLowerLevel()).isEqualTo("Court of Protection");
        assertThat(secondUserAuthorisation1.getAppointmentId()).isEqualTo("25512");
        assertThat(secondUserAuthorisation1.getAuthorisationId()).isEqualTo("9920");
        assertThat(secondUserAuthorisation1.getJurisdictionId()).isEqualTo("26");

        if (size == 4) {
            Authorisation secondUserAuthorisation2 = authorisations.get(3);
            assertThat(secondUserAuthorisation2).isNotNull();

            assertThat(secondUserAuthorisation2.getPersonalCode()).isEqualTo("4913089");
            assertThat(secondUserAuthorisation2.getJurisdiction()).isEqualTo("Tribunals");
            assertThat(secondUserAuthorisation2.getStartDate()).isEqualTo("2007-05-11");
            assertThat(secondUserAuthorisation2.getEndDate()).isEqualTo("2014-06-20");
            assertThat(secondUserAuthorisation2.getCreatedDate()).isNotNull();
            assertThat(secondUserAuthorisation2.getLastUpdated()).isNotNull();
            assertThat(secondUserAuthorisation2.getTicketCode()).isEqualTo("328");
            assertThat(secondUserAuthorisation2.getLowerLevel())
                    .isEqualTo("Criminal Injuries Compensations");
            assertThat(secondUserAuthorisation2.getAppointmentId()).isEqualTo("25513");
            assertThat(secondUserAuthorisation2.getAuthorisationId()).isEqualTo("14722");
            assertThat(secondUserAuthorisation2.getJurisdictionId()).isEqualTo("27");
        }
    }

    protected void verifyUserJudiciaryRolesData(int size) {
        final List<JudicialRoleType> judicialRoleTypes = judicialRoleTypeRepository.findAll();
        assertThat(judicialRoleTypes).isNotNull().isNotEmpty().hasSize(size);

        final JudicialRoleType firstUserJudicialRole = judicialRoleTypes.get(0);
        assertThat(firstUserJudicialRole).isNotNull();
        verifyFirstUserJudicialRoleData(firstUserJudicialRole);

        if (size == 2) {
            final JudicialRoleType secondUserJudicialRole = judicialRoleTypes.get(1);
            assertThat(secondUserJudicialRole).isNotNull();
            verifySecondUserJudicialRoleData(secondUserJudicialRole);
        }
    }

    private void verifyFirstUserJudicialRoleData(JudicialRoleType firstUserJudicialRole) {
        assertThat(firstUserJudicialRole.getPersonalCode()).isEqualTo("4913085");
        assertThat(firstUserJudicialRole.getJurisdictionRoleId()).isEqualTo("427");
        assertThat(firstUserJudicialRole.getJurisdictionRoleNameId()).isEqualTo("fee");
        assertThat(firstUserJudicialRole.getTitle()).isEqualTo("Course Director for COP (JC)");
        assertThat(firstUserJudicialRole.getStartDate())
                .isEqualTo(formatDate("2016-04-30T00:00:00.000Z"));
        assertThat(firstUserJudicialRole.getEndDate())
                .isEqualTo(formatDate("2019-04-30T00:00:00.000Z"));
    }

    private void verifySecondUserJudicialRoleData(JudicialRoleType secondUserJudicialRole) {
        assertThat(secondUserJudicialRole.getPersonalCode()).isEqualTo("4913089");
        assertThat(secondUserJudicialRole.getJurisdictionRoleId()).isEqualTo("428");
        assertThat(secondUserJudicialRole.getJurisdictionRoleNameId()).isEqualTo("fee");
        assertThat(secondUserJudicialRole.getTitle()).isEqualTo("Course Director for COP (JC)");
        assertThat(secondUserJudicialRole.getStartDate())
                .isEqualTo(formatDate("2017-03-10T00:00:00.000Z"));
        assertThat(secondUserJudicialRole.getEndDate())
                .isEqualTo(formatDate("2019-03-10T00:00:00.000Z"));
    }

    protected void verifyLocationData() {
        final List<BaseLocation> baseLocations = baseLocationRepository.findAll()
                .stream()
                .sorted(comparingInt(baseLocation -> parseInt(baseLocation.getBaseLocationId())))
                .toList();

        assertThat(baseLocations).isNotNull().isNotEmpty().hasSize(8);
        final BaseLocation baseLocation1 = baseLocations.get(0);
        assertThat(baseLocation1).isNotNull();
        assertThat(baseLocation1.getBaseLocationId()).isNotNull().isEqualTo("1");
        assertThat(baseLocation1.getName()).isNotNull().isEqualTo("Aberconwy");
        assertThat(baseLocation1.getTypeId()).isNotNull().isEqualTo("46");
        assertThat(baseLocation1.getParentId()).isNotNull().isEqualTo("1722");
        assertThat(baseLocation1.getJurisdictionId()).isNotNull().isEqualTo("28");
        assertThat(baseLocation1.getStartDate()).isNull();
        assertThat(baseLocation1.getEndDate()).isNull();
        assertThat(baseLocation1.getCreatedAt()).isNotNull().isEqualTo("2023-01-12T16:42:35");
        assertThat(baseLocation1.getUpdatedAt()).isNotNull().isEqualTo("2023-01-12T16:42:35");

        final BaseLocation baseLocation2 = baseLocations.get(1);
        assertThat(baseLocation2).isNotNull();
        assertThat(baseLocation2.getBaseLocationId()).isNotNull().isEqualTo("3");
        assertThat(baseLocation2.getName()).isNotNull().isEqualTo("Alnwick");
        assertThat(baseLocation2.getTypeId()).isNotNull().isEqualTo("46");
        assertThat(baseLocation2.getParentId()).isNotNull().isEqualTo("1742");
        assertThat(baseLocation2.getJurisdictionId()).isNotNull().isEqualTo("28");
        assertThat(baseLocation2.getStartDate()).isNull();
        assertThat(baseLocation2.getEndDate()).isNull();
        assertThat(baseLocation2.getCreatedAt()).isNotNull().isEqualTo("2023-02-12T16:42:35");
        assertThat(baseLocation2.getUpdatedAt()).isNotNull().isEqualTo("2023-02-12T16:42:35");

        final BaseLocation baseLocation3 = baseLocations.get(2);
        assertThat(baseLocation3).isNotNull();
        assertThat(baseLocation3.getBaseLocationId()).isNotNull().isEqualTo("5");
        assertThat(baseLocation3.getName()).isNotNull().isEqualTo("Appleby");
        assertThat(baseLocation3.getTypeId()).isNotNull().isEqualTo("46");
        assertThat(baseLocation3.getParentId()).isNotNull().isEqualTo("1703");
        assertThat(baseLocation3.getJurisdictionId()).isNotNull().isEqualTo("28");
        assertThat(baseLocation3.getStartDate()).isNull();
        assertThat(baseLocation3.getEndDate()).isNull();
        assertThat(baseLocation3.getCreatedAt()).isNotNull().isEqualTo("2023-03-12T16:42:36");
        assertThat(baseLocation3.getUpdatedAt()).isNotNull().isEqualTo("2023-03-12T16:42:36");

        final BaseLocation baseLocation4 = baseLocations.get(3);
        assertThat(baseLocation4).isNotNull();
        assertThat(baseLocation4.getBaseLocationId()).isNotNull().isEqualTo("6");
        assertThat(baseLocation4.getName()).isNotNull().isEqualTo("Arfon");
        assertThat(baseLocation4.getTypeId()).isNotNull().isEqualTo("46");
        assertThat(baseLocation4.getParentId()).isNotNull().isEqualTo("1722");
        assertThat(baseLocation4.getJurisdictionId()).isNotNull().isEqualTo("28");
        assertThat(baseLocation4.getStartDate()).isNull();
        assertThat(baseLocation4.getEndDate()).isNull();
        assertThat(baseLocation4.getCreatedAt()).isNotNull().isEqualTo("2023-04-12T16:42:36");
        assertThat(baseLocation4.getUpdatedAt()).isNotNull().isEqualTo("2023-04-12T16:42:36");

        final BaseLocation baseLocation5 = baseLocations.get(4);
        assertThat(baseLocation5).isNotNull();
        assertThat(baseLocation5.getBaseLocationId()).isNotNull().isEqualTo("7");
        assertThat(baseLocation5.getName()).isNotNull().isEqualTo("Arundel");
        assertThat(baseLocation5.getTypeId()).isNotNull().isEqualTo("46");
        assertThat(baseLocation5.getParentId()).isNotNull().isEqualTo("1815");
        assertThat(baseLocation5.getJurisdictionId()).isNotNull().isEqualTo("28");
        assertThat(baseLocation5.getStartDate()).isNull();
        assertThat(baseLocation5.getEndDate()).isNull();
        assertThat(baseLocation5.getCreatedAt()).isNotNull().isEqualTo("2023-05-12T16:42:37");
        assertThat(baseLocation5.getUpdatedAt()).isNotNull().isEqualTo("2023-05-12T16:42:37");

        final BaseLocation baseLocation6 = baseLocations.get(5);
        assertThat(baseLocation6).isNotNull();
        assertThat(baseLocation6.getBaseLocationId()).isNotNull().isEqualTo("768");
        assertThat(baseLocation6.getName()).isNotNull().isEqualTo("Arundelsss");
        assertThat(baseLocation6.getTypeId()).isNotNull().isEqualTo("46");
        assertThat(baseLocation6.getParentId()).isNotNull().isEqualTo("1816");
        assertThat(baseLocation6.getJurisdictionId()).isNotNull().isEqualTo("28");
        assertThat(baseLocation6.getStartDate()).isNull();
        assertThat(baseLocation6.getEndDate()).isNull();
        assertThat(baseLocation6.getCreatedAt()).isNotNull().isEqualTo("2023-06-12T16:42:37");
        assertThat(baseLocation6.getUpdatedAt()).isNotNull().isEqualTo("2023-06-12T16:42:37");

        final BaseLocation baseLocation7 = baseLocations.get(6);
        assertThat(baseLocation7).isNotNull();
        assertThat(baseLocation7.getBaseLocationId()).isNotNull().isEqualTo("1815");
        assertThat(baseLocation7.getName()).isNotNull().isEqualTo("Oxford");
        assertThat(baseLocation7.getTypeId()).isNotNull().isEqualTo("49");
        assertThat(baseLocation7.getParentId()).isNotNull().isEqualTo("1817");
        assertThat(baseLocation7.getJurisdictionId()).isNotNull().isEqualTo("28");
        assertThat(baseLocation7.getStartDate()).isNull();
        assertThat(baseLocation7.getEndDate()).isNull();
        assertThat(baseLocation7.getCreatedAt()).isNotNull().isEqualTo("2023-07-12T16:42:37");
        assertThat(baseLocation7.getUpdatedAt()).isNotNull().isEqualTo("2023-07-12T16:42:37");

        final BaseLocation baseLocation8 = baseLocations.get(7);
        assertThat(baseLocation8).isNotNull();
        assertThat(baseLocation8.getBaseLocationId()).isNotNull().isEqualTo("2218");
        assertThat(baseLocation8.getName()).isNotNull().isEqualTo("Arundels");
        assertThat(baseLocation8.getTypeId()).isNotNull().isEqualTo("46");
        assertThat(baseLocation8.getParentId()).isNotNull().isEqualTo("1815");
        assertThat(baseLocation8.getJurisdictionId()).isNotNull().isEqualTo("28");
        assertThat(baseLocation8.getStartDate()).isNull();
        assertThat(baseLocation8.getEndDate()).isNull();
        assertThat(baseLocation8.getCreatedAt()).isNotNull().isEqualTo("2023-08-12T16:42:37");
        assertThat(baseLocation8.getUpdatedAt()).isNotNull().isEqualTo("2023-08-12T16:42:37");
    }

    private LocalDateTime formatDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSS_Z);
        return LocalDateTime.parse(date, formatter);
    }

    protected void deleteData() {
        elinkDataExceptionRepository.deleteAll();
        elinksResponsesRepository.deleteAll();
        elinkSchedularAuditRepository.deleteAll();
        authorisationsRepository.deleteAll();
        judicialRoleTypeRepository.deleteAll();
        appointmentsRepository.deleteAll();
        profileRepository.deleteAll();
        baseLocationRepository.deleteAll();
        dataloadSchedulerJobRepository.deleteAll();
        authorisationsRepositoryAudit.deleteAll();
        judicialRoleTypeRepositoryAudit.deleteAll();
        appointmentsRepositoryAudit.deleteAll();
        profileRepositoryAudit.deleteAll();
    }

    protected void runElinksDataLoadJob() {
        elinksApiJobScheduler.loadElinksJob();
        List<DataloadSchedulerJob> audits = dataloadSchedulerJobRepository.findAll();
        DataloadSchedulerJob jobDetails = audits.get(0);

        assertThat(jobDetails).isNotNull();
        assertThat(jobDetails.getPublishingStatus()).isNotNull();
        assertEquals(SUCCESS.getStatus(), jobDetails.getPublishingStatus());
    }

    protected TestDataArguments getTestDataArguments() {
        return
                TestDataArguments.builder()
                        .expectedAppointmentsSize(2)
                        .expectedAuthorisationSize(2)
                        .expectedRoleSize(1)
                        .expectedUserProfiles(1)
                        .expectedActiveFlag(false)
                        .expectedDeletedFlag(true)
                        .expectedDeletedOnDate("2022-07-10")
                        .expectedLastWorkingDate("2023-03-01")
                        .expectedJobStatus(SUCCESS)
                        .isAfterIdamElasticSearch(true)
                        .elasticSearchSidamId("6455c84c-e77d-4c4f-9759-bf4a93a8e972")
                        .build();
    }

    protected void verifyAudit() {

        final List<ElinkDataSchedularAudit> eLinksDataSchedulerAudits =
                elinkSchedularAuditRepository.findAll()
                        .stream()
                        .sorted(comparing(ElinkDataSchedularAudit::getApiName))
                        .toList();
        assertThat(eLinksDataSchedulerAudits).isNotNull().isNotEmpty().hasSize(8);

        final ElinkDataSchedularAudit auditEntry1 = eLinksDataSchedulerAudits.get(0);
        final ElinkDataSchedularAudit auditEntry2 = eLinksDataSchedulerAudits.get(1);
        final ElinkDataSchedularAudit auditEntry3 = eLinksDataSchedulerAudits.get(2);
        final ElinkDataSchedularAudit auditEntry4 = eLinksDataSchedulerAudits.get(3);
        final ElinkDataSchedularAudit auditEntry5 = eLinksDataSchedulerAudits.get(4);
        final ElinkDataSchedularAudit auditEntry6 = eLinksDataSchedulerAudits.get(5);
        final ElinkDataSchedularAudit auditEntry7 = eLinksDataSchedulerAudits.get(6);
        final ElinkDataSchedularAudit auditEntry8 = eLinksDataSchedulerAudits.get(7);

        assertThat(auditEntry1).isNotNull();
        assertThat(auditEntry2).isNotNull();
        assertThat(auditEntry3).isNotNull();
        assertThat(auditEntry4).isNotNull();

        assertThat(auditEntry1.getApiName()).isNotNull().isEqualTo(DELETEDAPI);
        assertThat(auditEntry1.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry1.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry1.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry1.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry2.getApiName()).isNotNull().isEqualTo(ELASTICSEARCH);
        assertThat(auditEntry2.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry2.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry2.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry2.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry3.getApiName()).isNotNull().isEqualTo(IDAMSEARCH);
        assertThat(auditEntry3.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry3.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry3.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry3.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry4.getApiName()).isNotNull().isEqualTo(LEAVERSAPI);
        assertThat(auditEntry4.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry4.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry4.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry4.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry5.getApiName()).isNotNull().isEqualTo(LOCATIONAPI);
        assertThat(auditEntry5.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry5.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry5.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry5.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry6.getApiName()).isNotNull().isEqualTo(PEOPLEAPI);
        assertThat(auditEntry6.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry6.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry6.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry6.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry7.getApiName()).isNotNull().isEqualTo(PUBLISHSIDAM);
        assertThat(auditEntry7.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry7.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry7.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry7.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry8.getApiName()).isNotNull().isEqualTo(PUBLISHSIDAM);
        assertThat(auditEntry8.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry8.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry8.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry8.getSchedulerEndTime()).isNotNull();
    }
}
