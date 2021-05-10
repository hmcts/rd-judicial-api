package uk.gov.hmcts.reform.judicialapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.repository.UserProfileRepository;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.reform.judicialapi.util.RefDataUtil.createPageableObject;

@Slf4j
@Service
public class JudicialUserServiceImpl implements JudicialUserService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Value("${defaultPageSize}")
    Integer defaultPageSize;

    @Override
    public ResponseEntity<Object> fetchJudicialUsers(Integer size, Integer page) {
        long startTimeForQuery = System.currentTimeMillis();
        Pageable pageable = createPageableObject(page, size, defaultPageSize,
                Sort.by(Sort.DEFAULT_DIRECTION, "officeAppointmentId"));
        Page<UserProfile> pagedUserProfiles = userProfileRepository.findAll(pageable);

        log.info("The query took {} milliseconds for {} records: ",
                System.currentTimeMillis() - startTimeForQuery, size);

        long startTimeForObjectConversion = System.currentTimeMillis();
        List<UserProfile> userProfiles = pagedUserProfiles.getContent();
        List<OrmResponse> ormResponses = userProfiles.stream()
                .map(OrmResponse::new)
                .collect(Collectors.toList());
        log.info("The object conversion took {} milliseconds: ",
                System.currentTimeMillis() - startTimeForObjectConversion);

        return ResponseEntity
                .status(200)
                .body(ormResponses);
    }

}
