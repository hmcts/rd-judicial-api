package uk.gov.hmcts.reform.judicialapi.elinks.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkDeletedWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkIdamWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.SchedulerJobStatusResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.service.impl.ELinksServiceImpl;
import uk.gov.hmcts.reform.judicialapi.elinks.service.impl.ElinksPeopleServiceImpl;
import uk.gov.hmcts.reform.judicialapi.elinks.service.impl.IdamElasticSearchServiceImpl;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DELETEDSUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSSUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"AbbreviationAsWordInName", "MemberName", "warnings"})
class ElinksControllerTest {

    @InjectMocks
    ElinksController eLinksController;


    @Mock
    ELinksServiceImpl eLinksService;

    @Mock
    ElinksPeopleServiceImpl elinksPeopleServiceImpl;

    @Mock
    IdamElasticSearchServiceImpl idamElasticSearchService;

    @Spy
    PublishSidamIdService publishSidamIdService;

    @Test
    void test_load_location_success() {

        ResponseEntity<ElinkBaseLocationWrapperResponse> responseEntity;

        ElinkBaseLocationWrapperResponse elinkBaseLocationWrapperResponse = new ElinkBaseLocationWrapperResponse();
        elinkBaseLocationWrapperResponse.setMessage(LOCATION_DATA_LOAD_SUCCESS);



        responseEntity = new ResponseEntity<>(
            elinkBaseLocationWrapperResponse,
                null,
                HttpStatus.OK
        );

        when(eLinksService.retrieveLocation()).thenReturn(responseEntity);

        ResponseEntity<ElinkBaseLocationWrapperResponse> actual = eLinksController.loadLocation();
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody().getMessage()).isEqualTo(LOCATION_DATA_LOAD_SUCCESS);

    }


    @Test
    void test_load_people_success() {

        ResponseEntity<ElinkPeopleWrapperResponse> responseEntity;

        ElinkPeopleWrapperResponse elinkPeopleWrapperResponse = new ElinkPeopleWrapperResponse();
        elinkPeopleWrapperResponse.setMessage(PEOPLE_DATA_LOAD_SUCCESS);

        responseEntity = new ResponseEntity<>(
                elinkPeopleWrapperResponse,
                null,
                HttpStatus.OK
        );

        when(elinksPeopleServiceImpl.updatePeople()).thenReturn(responseEntity);

        ResponseEntity<ElinkPeopleWrapperResponse> actual = eLinksController.loadPeople();
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody().getMessage()).hasToString(PEOPLE_DATA_LOAD_SUCCESS);

    }

    @Test
    void test_idam_elastic_search_success() {

        Set<IdamResponse> idamResponseSet = new HashSet<>();
        idamResponseSet.add(new IdamResponse());
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).body(idamResponseSet);
        when(idamElasticSearchService.getIdamElasticSearchSyncFeed()).thenReturn(response);

        ResponseEntity<Object> actual = eLinksController.idamElasticSearch();
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void test_idam_fetch() {

        ElinkIdamWrapperResponse elinkIdamWrapperResponse = new ElinkIdamWrapperResponse();
        elinkIdamWrapperResponse.setMessage(PEOPLE_DATA_LOAD_SUCCESS);

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).body(elinkIdamWrapperResponse);
        when(idamElasticSearchService.getIdamDetails()).thenReturn(response);

        ResponseEntity<Object> actual = eLinksController.fetchIdamIds();
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void test_load_leaver_success() {

        ResponseEntity<ElinkLeaversWrapperResponse> responseEntity;

        ElinkLeaversWrapperResponse elinkLeaversWrapperResponse = new ElinkLeaversWrapperResponse();
        elinkLeaversWrapperResponse.setMessage(LEAVERSSUCCESS);



        responseEntity = new ResponseEntity<>(
                elinkLeaversWrapperResponse,
                null,
                HttpStatus.OK
        );

        when(eLinksService.retrieveLeavers()).thenReturn(responseEntity);

        ResponseEntity<ElinkLeaversWrapperResponse> actual = eLinksController.loadLeavers();
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody().getMessage()).isEqualTo(LEAVERSSUCCESS);

    }

    @Test
    void test_load_deleted_success() {

        ResponseEntity<ElinkDeletedWrapperResponse> responseEntity;

        ElinkDeletedWrapperResponse elinkDeletedWrapperResponse = new ElinkDeletedWrapperResponse();
        elinkDeletedWrapperResponse.setMessage(DELETEDSUCCESS);

        responseEntity = new ResponseEntity<>(
            elinkDeletedWrapperResponse,
            null,
            HttpStatus.OK
        );

        when(eLinksService.retrieveDeleted()).thenReturn(responseEntity);

        ResponseEntity<ElinkDeletedWrapperResponse> actual = eLinksController.loadDeleted();
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody().getMessage()).isEqualTo(DELETEDSUCCESS);

    }


    @Test
    void test_publish_sidam_ids() {

        ResponseEntity<SchedulerJobStatusResponse> responseEntity;

        SchedulerJobStatusResponse schedulerJobStatusResponse = SchedulerJobStatusResponse.builder().id("id")
            .jobStatus("success").sidamIdsCount(10).statusCode(HttpStatus.OK.value()).build();

        responseEntity = new ResponseEntity<>(
            schedulerJobStatusResponse,
            null,
            HttpStatus.OK
        );

        when(publishSidamIdService.publishSidamIdToAsb()).thenReturn(responseEntity);

        ResponseEntity<SchedulerJobStatusResponse> actual = eLinksController.publishSidamIdToAsb();
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody().getJobStatus()).isEqualTo("success");

    }
}
