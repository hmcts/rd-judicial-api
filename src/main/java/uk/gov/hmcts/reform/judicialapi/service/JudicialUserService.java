package uk.gov.hmcts.reform.judicialapi.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface JudicialUserService {

    ResponseEntity<Object> fetchJudicialUsers(Integer size, Integer page, List<String> sidamIds);


    /**
     * Returns the User Profile for Refresh Assignments.
     *
     * @param serviceName serviceName
     * @param pageRequest pageRequest
     * @return UserProfile
     */
    ResponseEntity<Object> fetchUserProfileByServiceNames(String serviceName, PageRequest pageRequest);
}
