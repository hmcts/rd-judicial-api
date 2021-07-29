package uk.gov.hmcts.reform.judicialapi.client.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment implements Serializable {

    private Long officeAppointmentId;

    private Boolean isPrincipleAppointment;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean activeFlag;

    private LocalDateTime extractedDate;

    private LocalDateTime createdDate;

    private LocalDateTime lastLoadedDate;

    private BaseLocationType baseLocationType;

    @JsonIgnore
    private RegionType regionType;

    private String personalCode;

    @JsonIgnore
    private String perId;
}
