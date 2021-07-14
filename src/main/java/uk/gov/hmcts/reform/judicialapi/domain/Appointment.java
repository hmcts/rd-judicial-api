package uk.gov.hmcts.reform.judicialapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

@Entity(name = "judicial_office_appointment")
@Getter
@Setter
@NoArgsConstructor
public class Appointment implements Serializable {

    @Id
    @Column(name = "judicial_office_appointment_Id")
    private Long officeAppointmentId;

    @Column(name = "is_prinicple_appointment")
    private Boolean isPrincipleAppointment;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "active_flag")
    private Boolean activeFlag;

    @Column(name = "extracted_date")
    private LocalDateTime extractedDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_loaded_date")
    private LocalDateTime lastLoadedDate;

    @ManyToOne
    @JoinColumn(name = "per_Id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "base_location_Id")
    private BaseLocationType baseLocationType;

    @ManyToOne
    @JoinColumn(name = "region_Id")
    private RegionType regionType;

    @Column(name = "personal_code")
    @Size(max = 32)
    private String personalCode;
}
