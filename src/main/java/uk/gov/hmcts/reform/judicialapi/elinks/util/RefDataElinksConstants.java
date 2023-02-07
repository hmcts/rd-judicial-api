package uk.gov.hmcts.reform.judicialapi.elinks.util;

public class RefDataElinksConstants {

    private RefDataElinksConstants() {
    }

    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";

    public static final String BAD_REQUEST = "Syntax error or Bad Request";
    public static final String FORBIDDEN_ERROR = "Your source IP address is not whitelisted";
    public static final String UNAUTHORIZED_ERROR =
            "A valid access token hasn't been provided in the right form";
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

    public static final String JUDICIAL_REF_DATA_ELINKS = "judicial-ref-data-elinks";

    public static final String LEAVERSAPI = "Leavers";
    public static final String LOCATIONAPI = "Location";

    public static final String BASELOCATIONAPI = "BaseLocation";

    public enum JobStatus {
        IN_PROGRESS("IN_PROGRESS"),
        FAILED("FAILED"),
        SUCCESS("SUCCESS");
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
}
