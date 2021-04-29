package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class OrmResponse {

    @JsonProperty
    private String idamId;
    @JsonProperty
    private List<AppointmentResponse> appointments;
    @JsonProperty
    private List<AuthorisationResponse> authorisations;

    public OrmResponse(UserProfile userProfile) {
        this.idamId = UUID.randomUUID().toString();
        this.appointments = userProfile.getAppointments()
                .stream()
                .map(AppointmentResponse::new)
                .collect(Collectors.toList());
        this.authorisations = userProfile.getAuthorisations()
                .stream()
                .map(AuthorisationResponse::new)
                .collect(Collectors.toList());
    }

}
