package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ElinkUserService;
import uk.gov.hmcts.reform.judicialapi.repository.ServiceCodeMappingRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Setter
public class ElinkUserServiceImpl implements ElinkUserService {

    @Autowired
    private ProfileRepository userProfileRepository;

    @Autowired
    private ServiceCodeMappingRepository serviceCodeMappingRepository;

    @Value("${search.serviceCode}")
    private List<String> searchServiceCode;

    @Override
    public ResponseEntity<Object> retrieveElinkUsers(UserSearchRequest userSearchRequest) {
        var ticketCode = new ArrayList<String>();

        if (userSearchRequest.getServiceCode() != null) {
            var serviceCodeMappings = serviceCodeMappingRepository
                .findByServiceCodeIgnoreCase(userSearchRequest.getServiceCode());

            serviceCodeMappings
                .forEach(s -> ticketCode.add(s.getTicketCode()));
        }
        log.info("SearchServiceCode list = {}", searchServiceCode);
        var userSearchResponses = userProfileRepository
            .findBySearchForString(userSearchRequest.getSearchString().toLowerCase(),
                userSearchRequest.getServiceCode(), userSearchRequest.getLocation(), ticketCode,
                searchServiceCode);

        return ResponseEntity
            .status(200)
            .body(userSearchResponses);
    }
}
