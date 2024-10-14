package uk.gov.hmcts.reform.judicialapi.elinks.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;



@Entity(name = "judicialOfficeAppointment")
@Table(name = "judicial_office_appointment", schema = "dbjudicialdata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "judicial_office_appointment_id_sequence",
        sequenceName = "judicial_office_appointment_id_sequence",  schema = "dbjudicialdata", allocationSize = 1)
public class Appointment implements Serializable {

    @Id
    @Column(name = "judicial_office_appointment_Id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "judicial_office_appointment_id_sequence")
    private Long officeAppointmentId;

    @Column(name = "personal_code")
    @Size(max = 32)
    private String personalCode;

    @Column(name = "base_location_Id")
    @Size(max = 64)
    private String baseLocationId;

    @Column(name = "hmcts_region_id")
    @Size(max = 64)
    private String regionId;

    @Column(name = "is_prinicple_appointment")
    private Boolean isPrincipleAppointment;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_loaded_date")
    private LocalDateTime lastLoadedDate;

    @Column(name = "epimms_id")
    @Size(max = 16)
    private String epimmsId;

    @Column(name = "appointment")
    @Size(max = 256)
    private String appointmentMapping;

    @Column(name = "appointment_type")
    @Size(max = 32)
    private String appointmentType;

    @Column(name = "type")
    @Size(max = 32)
    private String type;

    @Column(name = "appointment_id")
    @Size(max = 256)
    private String appointmentId;

    @Column(name = "role_name_id")
    @Size(max = 256)
    private String roleNameId;

    @Column(name = "contract_type_id")
    @Size(max = 64)
    private String contractTypeId;

    @Column(name = "location")
    @Size(max = 64)
    private String location;

    @Column(name = "jo_base_location_id")
    @Size(max = 64)
    private String joBaseLocationId;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_code", referencedColumnName = "personal_code",
            insertable = false, updatable = false, nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "base_location_Id", referencedColumnName = "base_location_Id",
            insertable = false, updatable = false, nullable = false)
    private BaseLocation baseLocationType;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "hmcts_region_id", referencedColumnName = "hmcts_region_id",
            insertable = false, updatable = false, nullable = false)
    private RegionType regionType;

}
