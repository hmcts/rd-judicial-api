package uk.gov.hmcts.reform.judicialapi.client.domain;

import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile implements Serializable {

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

    private List<Appointment> appointments;

    private List<Authorisation> authorisations;


}
