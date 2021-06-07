package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.commons.lang.StringUtils.EMPTY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrmResponse {

    @JsonProperty
    private String idamId;
    @JsonProperty
    private List<AppointmentResponse> appointments;
    @JsonProperty
    private List<AuthorisationResponse> authorisations;

    public OrmResponse(UserProfile userProfile) {
        this.idamId = isNull(userProfile.getSidamId()) ? EMPTY : userProfile.getSidamId();
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
