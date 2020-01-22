package uk.gov.hmcts.reform.judicialapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.controller.response.JudicialUserProfileResponse;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;
import uk.gov.hmcts.reform.judicialapi.util.AuthorizationEnabledIntegrationTest;

@Slf4j
public class RetrieveJudicialUserProfileTest  extends AuthorizationEnabledIntegrationTest {

    @Before
    public void setUp() {

        LocalDateTime createdDate = LocalDateTime.of(2015, Month.APRIL, 29, 19, 30, 40);
        LocalDateTime joinedDate = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        LocalDateTime extractedDate = LocalDateTime.of(2015, Month.AUGUST, 29, 19, 30, 40);
        LocalDateTime lastWorkingDate = LocalDateTime.of(2015, Month.DECEMBER, 29, 19, 30, 40);
        LocalDateTime lastLoadedDate = LocalDateTime.of(2015, Month.DECEMBER, 29, 19, 30, 40);


        JudicialUserProfile judicialUserProfile = new JudicialUserProfile("12345","54621","judge","KnownAs",
                "surName", "fullName","postNominal","contractType","workPattern",
                "somewhere@net.com",joinedDate,lastWorkingDate,true,extractedDate,createdDate,lastLoadedDate);

        judicialUserProfileRepository.save(judicialUserProfile);

    }

    @Test
    public void search_returns_judicial_user_profile_with_emailId() {


        Map<String, Object> response = judicialReferenceDataClient.findJudicialUserProfileByUserEmail("somewhere@net.com",caseworker);

        assertThat(response.get("http_status")).isEqualTo("200 OK");
        assertThat(((List<JudicialUserProfileResponse>) response.get("users")).size()).isGreaterThan(0);

        List<HashMap> judicialUsersProfileResponses = (List<HashMap>) response.get("users");
        HashMap judicialUsersProfileResponse = judicialUsersProfileResponses.get(0);

        assertThat(judicialUsersProfileResponse.get("fullname")).isNotNull();
        assertThat(judicialUsersProfileResponse.get("surname")).isNotNull();
        assertThat(judicialUsersProfileResponse.get("lastName")).isNotNull();
        assertThat(judicialUsersProfileResponse.get("emailID")).isNotNull();
        assertThat(judicialUsersProfileResponse.get("title")).isNotNull();
    }

    @Test
    public void returns_404_when_email_not_found() {
        Map<String, Object> response =
                judicialReferenceDataClient.findJudicialUserProfileByUserEmail("somewhere@net.com", caseworker);

        assertThat(response.get("http_status")).isEqualTo("404");
    }

    public void user_with_non_caseworker_role_cannot_retrieve_judicial_user_profile() {
        Map<String, Object> response = judicialReferenceDataClient.findJudicialUserProfileByUserEmail("somewhere@net.com",puiOrgManager);
        log.info("response::::::" + response);
        assertThat(response.get("http_status")).isEqualTo("403");
    }
}
