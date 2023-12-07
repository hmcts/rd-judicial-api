package uk.gov.hmcts.reform.judicialapi.elinks.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import static javax.persistence.CascadeType.ALL;


@Entity(name = "judicialUserProfile")
@Table(name = "judicial_user_profile", schema = "dbjudicialdata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile implements Serializable {

    @Id
    @Column(name = "personal_code")
    @Size(max = 32)
    private String personalCode;

    @Column(name = "known_as")
    @Size(max = 256)
    private String knownAs;

    @Column(name = "surname")
    @Size(max = 256)
    private String surname;

    @Column(name = "full_name")
    @Size(max = 256)
    private String fullName;

    @Column(name = "post_nominals")
    @Size(max = 256)
    private String postNominals;

    @Column(name = "ejudiciary_email")
    @Size(max = 256)
    private String emailId;

    @Column(name = "last_working_date")
    private LocalDate lastWorkingDate;

    @Column(name = "active_flag")
    private Boolean activeFlag;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_loaded_date")
    private LocalDateTime lastLoadedDate;

    @Column(name = "object_id")
    @Size(max = 64)
    private String objectId;

    @Column(name = "sidam_id")
    @Size(max = 64)
    private String sidamId;

    @Column(name = "initials")
    @Size(max = 256)
    private String initials;

    @Column(name = "title")
    @Size(max = 256)
    private String title;

    @Column(name = "retirement_date")
    private LocalDate retirementDate;



    @Column(name = "deleted_flag")
    private Boolean deletedFlag;

    @Column(name = "date_of_deletion")
    private LocalDateTime deletedOn;


    @OneToMany(targetEntity = Appointment.class, mappedBy = "userProfile", cascade = ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Appointment> appointments;

    @OneToMany(targetEntity = Authorisation.class, mappedBy = "userProfile", cascade = ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Authorisation> authorisations;

    @OneToMany(targetEntity = JudicialRoleType.class, mappedBy = "userProfile", cascade = ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<JudicialRoleType> judicialRoleTypes;


}
