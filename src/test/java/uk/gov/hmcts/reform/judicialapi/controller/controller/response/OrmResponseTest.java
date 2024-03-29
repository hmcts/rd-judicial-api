package uk.gov.hmcts.reform.judicialapi.controller.controller.response;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.controller.response.AppointmentResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.AuthorisationResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createUserProfile;

class OrmResponseTest {

    @Test
    void test_OrmResponseTest() {
        UserProfile userProfile = createUserProfile();

        OrmResponse ormResponse = new OrmResponse(userProfile);

        assertThat(ormResponse.getIdamId()).isEqualTo(userProfile.getSidamId());
        assertThat(ormResponse.getAppointments()).hasSize(1);
        assertThat(ormResponse.getAuthorisations()).hasSize(1);

    }

    @Test
    void test_OrmResponseSetter() {
        OrmResponse ormResponse = new OrmResponse();
        List<AppointmentResponse> appointmentResponseList = Collections.singletonList(new AppointmentResponse());
        List<AuthorisationResponse> authorisationResponseList = Collections.singletonList(new AuthorisationResponse());

        ormResponse.setIdamId("sidamId");
        ormResponse.setAppointments(appointmentResponseList);
        ormResponse.setAuthorisations(authorisationResponseList);

        assertThat(ormResponse.getIdamId()).isEqualTo("sidamId");
        assertThat(ormResponse.getAppointments()).isEqualTo(appointmentResponseList);
        assertThat(ormResponse.getAuthorisations()).isEqualTo(authorisationResponseList);

    }

}
