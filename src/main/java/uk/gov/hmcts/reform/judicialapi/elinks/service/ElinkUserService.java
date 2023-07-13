package uk.gov.hmcts.reform.judicialapi.elinks.service;

import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;

public interface ElinkUserService {
    ResponseEntity<Object> retrieveElinkUsers(UserSearchRequest userSearchRequest);
}
