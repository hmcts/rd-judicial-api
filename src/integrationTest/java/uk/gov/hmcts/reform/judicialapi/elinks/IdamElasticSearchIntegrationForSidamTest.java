package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unchecked")
class IdamElasticSearchIntegrationForSidamTest extends ElinksEnabledIntegrationTest {


    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    IdamTokenConfigProperties tokenConfigProperties;
    @Autowired
    AuthorisationsRepository authorisationsRepository;
    @Autowired
    AppointmentsRepository appointmentsRepository;

    @Autowired
    private ElinkSchedularAuditRepository elinkSchedularAuditRepository;


    @BeforeEach
    void setUp() {

        final String clientId = "234342332";
        final String redirectUri = "http://idam-api.aat.platform.hmcts.net";
        //The authorization and clientAuth is the dummy value which we can evaluate using BASE64 encoder.
        final String authorization = "ZHVtbXl2YWx1ZUBobWN0cy5uZXQ6SE1DVFMxMjM0";
        final String clientAuth = "cmQteHl6LWFwaTp4eXo=";
        final String url = "http://127.0.0.1:5000";
        tokenConfigProperties.setClientId(clientId);
        tokenConfigProperties.setClientAuthorization(clientAuth);
        tokenConfigProperties.setAuthorization(authorization);
        tokenConfigProperties.setRedirectUri(redirectUri);
        tokenConfigProperties.setUrl(url);

        cleanupData();
    }


    @BeforeAll
    void loadElinksResponse() throws Exception {

        cleanupData();


        String peopleResponseValidationJson =
            loadJson("src/integrationTest/resources/wiremock_responses/people_part.json");

        elinks.stubFor(get(urlPathMatching("/people"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", V2.MediaType.SERVICE)
                .withHeader("Connection", "close")
                .withBody(peopleResponseValidationJson)));

        String idamResponseValidationJson =
            loadJson("src/integrationTest/resources/wiremock_responses/sidamid_update_for_matched_objectid.json");

        sidamService.stubFor(get(urlPathMatching("/api/v1/users"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withHeader("Connection", "close")
                .withBody(idamResponseValidationJson)
            ));
    }

    @AfterEach
    void cleanUp() {
        cleanupData();
    }


    @DisplayName("Sidam Id should be updated for matched object Id")
    @Test
    void sidam_Id_Update_For_Matched_object_Id_From_Null_To_Value()  {

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");


        List<UserProfile> userprofile = profileRepository.findAll();
        assertEquals(2, userprofile.size());
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cae", userprofile.get(0).getObjectId());
        assertEquals(null,userprofile.get(0).getSidamId());

        Map<String, Object> idamResponses = elinksReferenceDataClient.getIdamElasticSearch();
        assertEquals("200 OK",idamResponses.get("http_status"));
        List<IdamResponse> idamResponse = (ArrayList<IdamResponse>) idamResponses.get("body");
        assertEquals(2,idamResponse.size());

        userprofile = profileRepository.findAll();
        assertEquals(2, userprofile.size());
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cae", userprofile.get(0).getObjectId());
        assertEquals("6455c84c-e77d-4c4f-9759-bf4a93a8e972", userprofile.get(0).getSidamId());

        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cab", userprofile.get(1).getObjectId());
        assertEquals("6455c84c-e77d-4c4f-9759-bf4a93a8e974", userprofile.get(1).getSidamId());

    }

    @DisplayName("Sidam Id should be updated for matched object Id from V1 to v2")
    @Test
    void sidam_Id_Update_For_Matched_object_Id_From_V1_To_V2()  {
        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");

        List<UserProfile> userprofile = profileRepository.findAll();
        userprofile.get(1).setSidamId("6455c84c-e77d-4c4f-9759-bf4a93a8e970");
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cab", userprofile.get(1).getObjectId());
        assertEquals("6455c84c-e77d-4c4f-9759-bf4a93a8e970", userprofile.get(1).getSidamId());

        Map<String, Object> idamResponses = elinksReferenceDataClient.getIdamElasticSearch();
        assertEquals("200 OK",idamResponses.get("http_status"));
        List<IdamResponse> idamResponse = (ArrayList<IdamResponse>) idamResponses.get("body");
        assertEquals(2,idamResponse.size());

        userprofile = profileRepository.findAll();
        assertEquals(2, userprofile.size());
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cab", userprofile.get(1).getObjectId());
        assertEquals("6455c84c-e77d-4c4f-9759-bf4a93a8e974", userprofile.get(1).getSidamId());

        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cae", userprofile.get(0).getObjectId());
        assertEquals("6455c84c-e77d-4c4f-9759-bf4a93a8e972", userprofile.get(0).getSidamId());

    }


    @DisplayName("Sidam Id should be updated for matched object Id No change")
    @Test
    void sidam_Id_Update_For_Matched_object_Id_No_Change()  {
        Map<String, Object> response = elinksReferenceDataClient.getPeoples();
        assertThat(response).containsEntry("http_status", "200 OK");

        Map<String, Object> idamResponses = elinksReferenceDataClient.getIdamElasticSearch();
        assertEquals("200 OK",idamResponses.get("http_status"));
        List<IdamResponse> idamResponse = (ArrayList<IdamResponse>) idamResponses.get("body");
        assertEquals(2,idamResponse.size());
        List<UserProfile> userprofile = profileRepository.findAll();
        assertEquals(2, userprofile.size());
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cae", userprofile.get(0).getObjectId());
        assertEquals("6455c84c-e77d-4c4f-9759-bf4a93a8e972", userprofile.get(0).getSidamId());

        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cab", userprofile.get(1).getObjectId());
        assertEquals("6455c84c-e77d-4c4f-9759-bf4a93a8e974", userprofile.get(1).getSidamId());


    }

    protected void cleanupData() {
        authorisationsRepository.deleteAll();
        appointmentsRepository.deleteAll();
        profileRepository.deleteAll();
        elinkSchedularAuditRepository.deleteAll();
    }
}
