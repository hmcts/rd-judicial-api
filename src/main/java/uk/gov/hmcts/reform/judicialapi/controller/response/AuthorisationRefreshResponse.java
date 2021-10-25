package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorisationRefreshResponse implements Serializable {

    @JsonIgnore
    private String perId;
    @JsonIgnore
    private String officeAuthId;
    private String jurisdiction;
    private String ticketDescription;
    private String ticketCode;
    private String startDate;
    private String endDate;

}
