package uk.gov.hmcts.reform.judicialapi.service;

import org.springframework.http.ResponseEntity;


public interface JudicialUserService {

    ResponseEntity<Object> fetchJudicialUsers(Integer size, Integer page);
}
