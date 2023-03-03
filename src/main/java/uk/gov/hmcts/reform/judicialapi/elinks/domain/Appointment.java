package uk.gov.hmcts.reform.judicialapi.elinks.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    @Column(name = "region_Id")
    @Size(max = 256)
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

    @Column(name = "service_code")
    @Size(max = 64)
    private String serviceCode;

    @Column(name = "object_id")
    @Size(max = 64)
    private String objectId;

    @Column(name = "appointment")
    @Size(max = 64)
    private String appointmentRolesMapping;

    @Column(name = "appointment_type")
    @Size(max = 32)
    private String appointmentType;

    @Column(name = "work_pattern")
    @Size(max = 64)
    private String workPattern;


}
