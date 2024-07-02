package uk.gov.hmcts.reform.judicialapi.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper;
import uk.gov.hmcts.reform.judicialapi.idam.IdamOpenIdClient;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import java.util.List;

@Slf4j
public class JudicialApiClient {

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String SERVICE_HEADER = "ServiceAuthorization";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final String judicialApiUrl;
    private final String s2sToken;
    private final IdamOpenIdClient idamOpenIdClient;
    private static final String USERS_SEARCH_URI = "/refdata/judicial/users/search";
    private static final String REFRESH_ROLE_URI = "/refdata/judicial/users";


    public JudicialApiClient(String judicialApiUrl,
                             String s2sToken,
                             IdamOpenIdClient idamOpenIdClient) {
        this.judicialApiUrl = judicialApiUrl;
        this.s2sToken = s2sToken;
        this.idamOpenIdClient = idamOpenIdClient;
    }

    public RequestSpecification getMultipleAuthHeadersInternal(String role) {
        return getMultipleAuthHeaders(idamOpenIdClient.getcwdAdminOpenIdToken(role));
    }

    public RequestSpecification getMultipleAuthHeaders(String role,int pageSize, int pageNumber,
                                                       String sortColumn,String sortDirection) {
        String userToken = idamOpenIdClient.getcwdAdminOpenIdToken(role);
        return SerenityRest.with()
                .relaxedHTTPSValidation()
                .baseUri(judicialApiUrl)
                .header("Content-Type", V2.MediaType.SERVICE)
                .header("Accepts", V2.MediaType.SERVICE)
                .header(SERVICE_HEADER, "Bearer " + s2sToken)
                .header(AUTHORIZATION_HEADER, "Bearer " + userToken)
                .header("page_size", pageSize)
                .header("page_number", pageNumber)
                .header("sort_column", sortColumn)
                .header("sort_direction", sortDirection);
    }

    public RequestSpecification getMultipleAuthHeaders(String userToken) {
        return SerenityRest.with()
                .relaxedHTTPSValidation()
                .baseUri(judicialApiUrl)
                .header("Content-Type", V2.MediaType.SERVICE)
                .header("Accepts", V2.MediaType.SERVICE)
                .header(SERVICE_HEADER, "Bearer " + s2sToken)
                .header(AUTHORIZATION_HEADER, "Bearer " + userToken);
    }

    public Object userSearch(UserSearchRequest userSearchRequest, String role, HttpStatus expectedStatus) {
        var fetchResponse = getMultipleAuthHeadersInternal(role)
                .body(userSearchRequest).log().body(true)
                .post(USERS_SEARCH_URI)
                .andReturn();

        fetchResponse.then()
                .assertThat()
                .statusCode(expectedStatus.value());

        if (expectedStatus.is2xxSuccessful()) {
            return List.of(fetchResponse.getBody().as(UserSearchResponseWrapper[].class));
        } else {
            return fetchResponse.getBody().as(ErrorResponse.class);
        }
    }

    public Response refreshUserProfiles(RefreshRoleRequest refreshRoleRequest, int pageSize, int pageNumber,
                                        String sortColumn, String sortDirection,
                                        String role) {

        Response refreshResponse = getMultipleAuthHeaders(role,pageSize,pageNumber,sortColumn,sortDirection)
                .body(refreshRoleRequest).log().body(true)
                .post(REFRESH_ROLE_URI)
                .andReturn();

        log.info("JRD get refreshResponse status code: {}", refreshResponse.getStatusCode());

        return refreshResponse;
    }

}
