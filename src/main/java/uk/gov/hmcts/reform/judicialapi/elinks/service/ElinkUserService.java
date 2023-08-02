package uk.gov.hmcts.reform.judicialapi.elinks.service;

import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;

public interface ElinkUserService {
    ResponseEntity<Object> retrieveElinkUsers(UserSearchRequest userSearchRequest);

    ResponseEntity<Object> refreshUserProfile(RefreshRoleRequest refreshRoleRequest, Integer pageSize,
                                              Integer pageNumber, String sortDirection, String sortColumn);


}
