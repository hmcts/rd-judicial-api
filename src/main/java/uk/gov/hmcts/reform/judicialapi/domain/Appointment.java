package uk.gov.hmcts.reform.judicialapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;
import javax.persistence.CascadeType;

@Entity(name = "judicial_office_appointment")
@Getter
@Setter
@NoArgsConstructor
public class Appointment implements Serializable {

    @Id
    @Column(name = "judicial_office_appointment_Id")
    private Long officeAppointmentId;

    @Column(name = "per_id")
    private String perId;

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

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "per_id", referencedColumnName = "per_id",
            insertable = false, updatable = false, nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "base_location_Id", referencedColumnName = "base_location_Id",
            insertable = false, updatable = false, nullable = false)
    private BaseLocationType baseLocationType;

    @ManyToOne
    @JoinColumn(name = "region_Id", referencedColumnName = "region_Id",
            insertable = false, updatable = false, nullable = false)
    private RegionType regionType;

    @Column(name = "personal_code")
    @Size(max = 32)
    private String personalCode;

    @Column(name = "epimms_id")
    private String epimmsId;

    @Column(name = "service_code")
    private String serviceCode;

    @Column(name = "object_id")
    private String objectId;

    @Column(name = "appointment")
    private String appointment;

    @Column(name = "appointment_type")
    private String appointmentType;

    @Column(name = "base_location_Id")
    private String baseLocationId;

    @Column(name = "region_Id")
    private String regionId;

}
