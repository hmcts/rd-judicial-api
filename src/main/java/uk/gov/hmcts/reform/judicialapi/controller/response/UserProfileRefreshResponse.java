package uk.gov.hmcts.reform.judicialapi.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRefreshResponse implements Serializable {

    private String perId;

    private String personalCode;

    private String appointment;

    private String knownAs;

    private String surname;

    private String fullName;

    private String postNominals;

    private String appointmentType;

    private String workPattern;

    private String ejudiciaryEmailId;

    private LocalDate joiningDate;

    private LocalDate lastWorkingDate;

    private Boolean activeFlag;

    private LocalDateTime extractedDate;

    private LocalDateTime createdDate;

    private LocalDateTime lastLoadedDate;

    private String objectId;

    private String sidamId;

    private List<AppointmentRefreshResponse> appointments;

    private List<AuthorisationRefreshResponse> authorisations;


}
