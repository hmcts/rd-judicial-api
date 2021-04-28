package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    public OrmResponse(String idamId, List<AppointmentResponse> appointments,
                                      List<AuthorisationResponse> authorisations) {
        this.idamId = idamId;
        this.appointments = appointments;
        this.authorisations = authorisations;
    }

}
