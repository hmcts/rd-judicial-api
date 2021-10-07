package uk.gov.hmcts.reform.judicialapi.controller.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRefreshResponse implements Serializable {

    private Long officeAppointmentId;

    private Boolean isPrincipleAppointment;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean activeFlag;

    private LocalDateTime extractedDate;

    private LocalDateTime createdDate;

    private LocalDateTime lastLoadedDate;

    private BaseLocationTypeRefreshResponse baseLocationType;

    @JsonIgnore
    private RegionTypeRefreshResponse regionType;

    private String personalCode;

    @JsonIgnore
    private String perId;
}
