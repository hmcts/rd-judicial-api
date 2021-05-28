package uk.gov.hmcts.reform.judicialapi.controller.controller.response;

import org.junit.Test;
import uk.gov.hmcts.reform.judicialapi.controller.response.AppointmentResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.AuthorisationResponse;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.judicialapi.controller.TestSupport.createUserProfile;

public class OrmResponseTest {

    @Test
    public void test_OrmResponseTest() {
        UserProfile userProfile = createUserProfile();

        OrmResponse ormResponse = new OrmResponse(userProfile);

        assertThat(ormResponse.getIdamId()).isEqualTo(userProfile.getSidamId());
        assertThat(ormResponse.getAppointments().size()).isEqualTo(1);
        assertThat(ormResponse.getAuthorisations().size()).isEqualTo(1);

    }

    @Test
    public void test_OrmResponseSetter() {
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
