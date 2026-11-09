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


@Entity(name = "judicialUserProfileAudit")
@Table(name = "judicial_user_profile_audit", schema = "dbjudicialdata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileAudit implements Serializable {

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

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
