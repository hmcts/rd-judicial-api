package uk.gov.hmcts.reform.judicialapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import javax.validation.constraints.Size;

import static javax.persistence.CascadeType.ALL;

@Builder
@Entity(name = "judicial_user_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile implements Serializable {

    @Id
    @Column(name = "per_Id")
    private String perId;

    @Column(name = "personal_code")
    @Size(max = 32)
    private String personalCode;

    @Column(name = "appointment")
    @Size(max = 64)
    private String appointment;

    @Column(name = "known_as")
    @Size(max = 64)
    private String knownAs;

    @Column(name = "surname")
    @Size(max = 256)
    private String surname;

    @Column(name = "full_name")
    @Size(max = 256)
    private String fullName;

    @Column(name = "post_nominals")
    @Size(max = 32)
    private String postNominals;

    @Column(name = "appointment_type")
    @Size(max = 32)
    private String appointmentType;

    @Column(name = "work_pattern")
    @Size(max = 32)
    private String workPattern;

    @Column(name = "ejudiciary_email")
    @Size(max = 256)
    private String ejudiciaryEmailId;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "last_working_date")
    private LocalDate lastWorkingDate;

    @Column(name = "active_flag")
    private Boolean activeFlag;

    @Column(name = "extracted_date")
    private LocalDateTime extractedDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_loaded_date")
    private LocalDateTime lastLoadedDate;

    @Column(name = "object_id")
    private String objectId;

    @Column(name = "sidam_id")
    private String sidamId;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(targetEntity = Appointment.class, mappedBy = "userProfile", cascade = ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(targetEntity = Authorisation.class, mappedBy = "userProfile", cascade = ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Authorisation> authorisations;


}
