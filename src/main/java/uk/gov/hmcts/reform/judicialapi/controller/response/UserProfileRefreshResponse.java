package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserProfileRefreshResponse implements Serializable {

    private String sidamId;

    private String objectId;

    private String knownAs;

    private String surname;

    private String fullName;

    private String postNominals;

    private String emailId;

    private String personalCode;

    private String isJudge;

    private String isPanelNumber;

    private String isMagistrate;

    private List<AppointmentRefreshResponse> appointments;

    private List<AuthorisationRefreshResponse> authorisations;

}
