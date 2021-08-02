package uk.gov.hmcts.reform.judicialapi.util;


@SuppressWarnings("HideUtilityClassConstructor")
public class RefDataConstants {


    public static final String BAD_REQUEST = "Bad Request";
    public static final String FORBIDDEN_ERROR = "Forbidden Error: Access denied for invalid permissions";
    public static final String UNAUTHORIZED_ERROR =
            "Unauthorized Error : The requested resource is restricted and requires authentication";
    public static final String NO_DATA_FOUND = "The User Profile data could not be found";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String REQUIRED_PARAMETER_SERVICE_NAMES_IS_EMPTY =
            "The required parameter 'serviceName' is empty";

    public static final String INVALID_FIELD = "The field %s is invalid. Please provide a valid value.";
    public static final String PAGE_NUMBER = "Page Number";
    public static final String PAGE_SIZE = "Page Size";
    public static final String SORT_DIRECTION = "Sort Direction";
    public static final String SORT_COLUMN = "Sort Column";
    public static final String API_IS_NOT_AVAILABLE_IN_PROD_ENV = "This API is not available in Production Environment";
    public static final String ERROR_IN_PARSING_THE_FEIGN_RESPONSE = "Error in parsing %s Feign Response";
    public static final String LRD_ERROR = "An error occurred while retrieving data from Location Reference Data";

    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    public static final String BEARER = "Bearer ";


}
