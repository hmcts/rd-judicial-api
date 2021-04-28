package uk.gov.hmcts.reform.judicialapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.judicialapi.controller.response.AppointmentResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.AuthorisationResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(
        path = "/refdata/judicial/users"
)

@RestController
public class JrdUsersController {


    @PostMapping(
            path = "/fetch",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> fetchUsers(@RequestParam(value = "page_size", required = false) Integer size,
                                             @RequestParam(value = "page_number", required = false) Integer page) {

        OrmResponse ormResponse = fetchJudicialUsers(size, page);

        return ResponseEntity
                .status(200)
                .body(ormResponse);
    }

    OrmResponse fetchJudicialUsers(Integer size, Integer page) {

        String idamID = UUID.randomUUID().toString();
        AppointmentResponse appointmentResponse = createAppointment();

        AuthorisationResponse authorisationResponse = createAuthorisation();

        List<AppointmentResponse> appointments = Collections.singletonList(appointmentResponse);
        List<AuthorisationResponse> authorisations = Collections.singletonList(authorisationResponse);

        return new OrmResponse(idamID, appointments, authorisations);

    }

    private AppointmentResponse createAppointment() {
        AppointmentResponse appt = new AppointmentResponse();
        appt.setAppointmentId("appId");
        appt.setRoleId("roleId");
        appt.setRoleDesc_En("roleDescEn");
        appt.setContractTypeId("ctrTypeId");
        appt.setContractTypeDescEn("ctrTypeIdDesc");
        appt.setBaseLocationId("baseLoc");
        appt.setRegionDescEn("locDesc");
        appt.setRegionId("regId");
        appt.setIsPrincipalAppointment("true");
        appt.setStartDate("23-04-21");
        appt.setEndDate("23-09-21");

        return appt;
    }

    private AuthorisationResponse createAuthorisation() {
        AuthorisationResponse auth = new AuthorisationResponse();
        auth.setAuthorisationId("authId");
        auth.setJurisdiction("jurisdiction");

        return auth;
    }

}
