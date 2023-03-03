package uk.gov.hmcts.reform.judicialapi.elinks.service;

import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;

public interface ElinksPeopleService {

    ResponseEntity<ElinkPeopleWrapperResponse> updatePeople();
}
