package uk.gov.hmcts.reform.judicialapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(
        path = "/refdata/judicial/users"
)

@RestController
public class JrdUsersController {


    @Autowired
    JudicialUserService judicialUserService;

    @PostMapping(
            path = "/fetch",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> fetchUsers(@RequestParam(value = "page_size", required = false) Integer size,
                                             @RequestParam(value = "page_number", required = false) Integer page) {

        return judicialUserService.fetchJudicialUsers(size, page);
    }


}
