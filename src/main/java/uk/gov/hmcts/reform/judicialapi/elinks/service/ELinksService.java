package uk.gov.hmcts.reform.judicialapi.elinks.service;

import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;

public interface ELinksService {

    ResponseEntity<ElinkBaseLocationWrapperResponse> retrieveBaseLocation();

    ResponseEntity<ElinkLocationWrapperResponse> retrieveLocation();

}
