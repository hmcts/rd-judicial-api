package uk.gov.hmcts.reform.judicialapi.elinks.util;

import java.util.List;

public class RefDataElinksConstants {

    private RefDataElinksConstants() {
    }

    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";

    public static final String BAD_REQUEST = "Syntax error or Bad Request";
    public static final String FORBIDDEN_ERROR = "Your source IP address is not whitelisted";
    public static final String UNAUTHORIZED_ERROR =
            "Invalid Access token has been provided. Please try with the right credentials";
    public static final String NO_DATA_FOUND = "The given attribute name does not exist in the reference data";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    public static final String TOO_MANY_REQUESTS = "You have exceeded the request limit (20 requests in 20 seconds)";

    public static final String LOCATION_DATA_LOAD_SUCCESS = "Location(RegionType) Loaded successfully";

    public static final String BASE_LOCATION_DATA_LOAD_SUCCESS = "Base Location Loaded successfully";

    public static final String ELINKS_ACCESS_ERROR = "An error occurred while retrieving data from Elinks";
    public static final String ELINKS_DATA_STORE_ERROR = "An error occurred while storing data from Elinks";
    public static final String ERROR_IN_PARSING_THE_FEIGN_RESPONSE = "Error in parsing %s Feign Response";

    public static final String ELINKS_ERROR_RESPONSE_BAD_REQUEST = "Syntax error or Bad request";
    public static final String ELINKS_ERROR_RESPONSE_UNAUTHORIZED =
            "A valid access token hasn't been provided in the right form";
    public static final String ELINKS_ERROR_RESPONSE_FORBIDDEN = "Your source IP address is not whitelisted";
    public static final String ELINKS_ERROR_RESPONSE_NOT_FOUND
            = "The given attribute name does not exist in the reference data";

    public static final String ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS
            = "You have exceeded the request limit (20 requests in 20 seconds)";

    public static final String PEOPLE_DATA_LOAD_SUCCESS = "People data loaded successfully";

    public static final String THREAD_INVOCATION_EXCEPTION = "An error occurred while thread sleeping";

    public static final String AUDIT_DATA_ERROR = "An error occurred while getting data from Audit table";

    public static final String DATA_UPDATE_ERROR = "An error occurred while updating data in refDataDb";

    public static final String REGION_DEFAULT_ID = "0";

    public static final String UPDATED_SINCE = "2015-01-01";

    public static final String LEAVERSSUCCESS = "Leavers Data Loaded Successfully";

    public static final String DELETEDSUCCESS = "Deleted users Data Loaded Successfully";

    public static final String JUDICIAL_REF_DATA_ELINKS = "judicial-ref-data-elinks";

    public static final String LEAVERSAPI = "Leavers";

    public static final String DELETEDAPI = "Deleted";

    public static final String LOCATIONAPI = "Location";

    public static final String PEOPLEAPI = "People";
    public static final String BASELOCATIONAPI = "BaseLocation";

    public static final String BASE_LOCATION_ID = "base_location_id";

    public static final String LOCATION = "Location";

    public static final String ROLENAME = "role_name";

    public static final String IDAMSEARCH = "Idam Search";

    public static final String PUBLISHASB = "Publish to ASB";

    public static final String JUDICIALROLETYPE = "judicial_role_type";

    public static final String USER_PROFILE = "jrd_user_profile";

    public static final String APPOINTMENT = "appointments";

    public static final String APPOINTMENTID = "appointmentId";

    public static final String EMAILID = "email_id";

    public static final String AUTHORISATION = "authorisations_with_dates";

    public static final String APPOINTMENTTYPE = "appointment type";


    public static final String LOCATIONIDFAILURE = "Appointment's Base Location ID : "
            + "is not available in location_type table";

    public static final String APPOINTMENTIDNOTAVAILABLE = "Appointment  ID : "
        + "is not available in Appointment Table";

    public static final String USERPROFILEEMAILID = "Personal Code : "
        + " is not having any email id";

    public static final String APPOINTMENTIDFAILURE = "Appointment  ID : "
        + "is failed to load";

    public static final String AUTHORISATIONIDFAILURE = "Appointment  ID : "
        + "is failed to load";

    public static final String USERPROFILEISPRESENT = "Personal  Code : "
        + "is already loaded";

    public static final String CFTREGIONIDFAILURE = "Location  : "
        + " is not available in jrd_lrd_region_mapping table";

    public static final String INVALIDROLENAMES = "Role Name  : "
        + " is invalid";

    public static final String USERPROFILEFAILURE = "UserProfile  : "
        + "is failed to load";

    public static final String APPOINTMENTTYPENOTMATCHING = "Appointment type  : "
        + "not matching";

    public static final String ERRORDESCRIPTIONFORINTTEST = "Appointment Base Location ID : 1000  "
            + "not available in BASE LOCATION table";

    public static final String APPOINTMENT_TABLE = "judicial_office_appointment";

    public static final String PERSONALCODE = "Personal_code";


    public static final String AUTHORISATION_TABLE = "judicial_office_authorisation";

    public static final String ASB_PUBLISH_SIDAM_ERROR = "An error occurred while Publishing SIDAM to ASB";

    public static final String DATABASE_FETCH_ERROR
        = "An error occurred while getting Details from database";

    public static final String JOB_DETAILS_UPDATE_ERROR
        = "An error occurred while updating asb job status in database";

    public static final String SEND_EMAIL_EXCEPTION
        = "An error occurred while sending mail";

    public static final String ASB_PUBLISH_TOPIC_ERROR
        = "An error occurred while Publishing message to service bus topic";

    public static final String SPTW
        = "SPTW-";

    public static final String FORBIDEN_ERROR = "Forbidden Error: Access denied for invalid permissions";

    public enum JobStatus {
        IN_PROGRESS("IN_PROGRESS"),
        FAILED("FAILED"),
        SUCCESS("SUCCESS"),
        PARTIAL_SUCCESS("PARTIAL_SUCCESS");
        String status;
        JobStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

    public static final String IDAM_ERROR_MESSAGE = "Error during the IDAM elastic search query";

    public static final String IDAM_TOKEN_ERROR_MESSAGE = "Idam Service Failed while bearer token generate";

    public static final List<String> INVALID_ROLES = List.of("CRTS TRIB - RS Admin User",
        "MAGS - AC Admin User","Person on a List","Unknown","Senior Coroner","Assistant Coroner",
        "Area Coroner","Acting Senior Coroner","Initial Automated Record");
}
