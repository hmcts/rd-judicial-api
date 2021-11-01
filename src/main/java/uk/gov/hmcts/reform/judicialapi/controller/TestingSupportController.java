package uk.gov.hmcts.reform.judicialapi.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;


@RestController
@ConditionalOnExpression("${testing.support.enabled:false}")
//@Api(tags = {SwaggerConfig.TESTING_SUPPORT})
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@RequestMapping(
        path = "/refdata/judicial/users"
)
public class TestingSupportController {

    @Autowired
    JudicialUserService judicialUserService;

    @GetMapping(path = "/testing-support/sidam/actions/create-users", produces = "application/json")
    @Secured({"jrd-system-user", "jrd-admin"})
    public ResponseEntity<Object> createIdamUserProfiles() {
        return judicialUserService.createIdamUserProfiles();
    }
}