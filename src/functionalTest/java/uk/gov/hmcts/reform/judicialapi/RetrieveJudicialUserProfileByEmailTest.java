package uk.gov.hmcts.reform.judicialapi;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;

import java.nio.file.Paths;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringIntegrationSerenityRunner.class)
@ActiveProfiles("functional")
@Slf4j
public class RetrieveJudicialUserProfileByEmailTest extends AuthorizationFunctionalTest {

    @BeforeClass
    public static void dbSetup() throws Exception {
        String loadFile = ResourceUtils.getFile("classpath:load-user-profile-functional.sql").getCanonicalPath();
        executeScript(ImmutableList.of(Paths.get(loadFile)));
    }

    @Test
    public void rdcc_751_ac1_user_with_appropriate_rights_can_retrieve_user_profile_with_email() {

        Map<String, Object> userProfileResponse = judicialApiClient.searchForUserByEmailAddress("james@demo.com", "caseworker");
        List<HashMap> judicialUserProfileResponse = (List<HashMap>) userProfileResponse.get("judicialUserProfileResponseList");
        judicialUserProfileResponse.stream().forEach(judicialUserProfile -> {
            assertThat("elinks_id").isNotNull();
            if (judicialUserProfile.get("elinks_id").equals("1")) {
                assertThat(judicialUserProfile.get("personalCode")).isEqualTo("personalCode");
            } else if (judicialUserProfile.get("title").equals("title")) {
                assertThat(judicialUserProfile.get("knownAs")).isEqualTo("knownAs");
            } else if (judicialUserProfile.get("surname").equals("surname")) {
                assertThat(judicialUserProfile.get("fullName")).isEqualTo("fullName");
            }
        });



  }

    @AfterClass
    public static void dbTearDown() throws Exception {
        String deleteFile = ResourceUtils.getFile("classpath:delete-user-profile-funct.sql").getCanonicalPath();
        executeScript(ImmutableList.of(Paths.get(deleteFile)));
    }


}


