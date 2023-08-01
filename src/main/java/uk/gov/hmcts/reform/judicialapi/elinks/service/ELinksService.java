package uk.gov.hmcts.reform.judicialapi.elinks.service;

import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkDeletedWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;

public interface ELinksService {

    ResponseEntity<ElinkBaseLocationWrapperResponse> retrieveLocation();

    ResponseEntity<ElinkLeaversWrapperResponse> retrieveLeavers();

    ResponseEntity<ElinkDeletedWrapperResponse> retrieveDeleted();

}
