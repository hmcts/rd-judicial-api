package uk.gov.hmcts.reform.judicialapi.elinks.domain.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity(name = "judicialOfficeAppointmentAudit")
@Table(name = "judicial_office_appointment_audit", schema = "dbjudicialdata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentAudit implements Serializable {

    @Id
    @Column(name = "judicial_office_appointment_Id")
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
    @Size(max = 64)
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
}
