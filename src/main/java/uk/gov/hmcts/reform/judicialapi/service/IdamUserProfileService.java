package uk.gov.hmcts.reform.judicialapi.service;

import org.springframework.http.ResponseEntity;

public interface IdamUserProfileService {
    ResponseEntity<Object> createIdamUserProfiles();
}
