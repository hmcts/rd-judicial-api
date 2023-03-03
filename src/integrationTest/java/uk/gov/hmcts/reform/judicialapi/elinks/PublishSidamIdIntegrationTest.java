package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doNothing;

class PublishSidamIdIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    private DataloadSchedulerJobRepository dataloadSchedulerJobRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    PublishSidamIdService publishSidamIdService;

    @MockBean
    ElinkTopicPublisher elinkTopicPublisher;

    @BeforeEach
    void setUp() {

        dataloadSchedulerJobRepository.deleteAll();
        profileRepository.deleteAll();
        profileRepository.save(buildUserProfileDto());


    }




    @DisplayName("Elinks Publish SidamId endpoint status verification success")
    @Test
    @Order(1)
    void test_publish_sidam_ids_status_success() {

        ReflectionTestUtils.setField(publishSidamIdService, "elinkTopicPublisher", elinkTopicPublisher);

        doNothing().when(elinkTopicPublisher).sendMessage(anyList(),anyString());;
        Map<String, Object> response = elinksReferenceDataClient.publishSidamIds();
        assertThat(response).containsEntry("http_status", "200 OK");
        HashMap publishSidamIdsResponse = (LinkedHashMap)response.get("body");

        assertThat(publishSidamIdsResponse.get("publishing_status")).isNotNull();

    }


    private UserProfile buildUserProfileDto() {

        return UserProfile.builder()
                .personalCode("0049931063")
                .knownAs("Tester")
                .surname("TestAccount")
                .fullName("Tribunal Judge Tester TestAccount 2")
                .postNominals("ABC")
                .ejudiciaryEmailId("Tester2@judiciarystaging.onmicrosoft.com")
                .lastWorkingDate(LocalDate.now())
                .activeFlag(true)
                .createdDate(LocalDateTime.now())
                .lastLoadedDate(LocalDateTime.now())
                .objectId("552da697-4b3d-4aed-9c22-1e903b70aead")
                .initials("Mr")
                .sidamId("3fa85f64-5717-4562-b3fc-2c963f66afa6")
                .build();
    }
}