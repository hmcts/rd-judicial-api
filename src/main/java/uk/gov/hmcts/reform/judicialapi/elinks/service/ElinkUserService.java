package uk.gov.hmcts.reform.judicialapi.elinks.service;

import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.UserSearchRequest;

public interface ElinkUserService {
    ResponseEntity<Object> retrieveElinkUsers(UserSearchRequest userSearchRequest);

    ResponseEntity<Object> refreshUserProfile(RefreshRoleRequest refreshRoleRequest, Integer pageSize,
                                              Integer pageNumber, String sortDirection, String sortColumn);


}
