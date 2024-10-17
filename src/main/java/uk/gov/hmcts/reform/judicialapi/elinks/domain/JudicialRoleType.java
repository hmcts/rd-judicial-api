package uk.gov.hmcts.reform.judicialapi.elinks.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "judicial_additional_roles")
@Table(name = "judicial_additional_roles", schema = "dbjudicialdata")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SequenceGenerator(name = "judicial_role_type_id_sequence",
        sequenceName = "judicial_role_type_id_sequence",  schema = "dbjudicialdata", allocationSize = 1)
public class JudicialRoleType {

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "judicial_role_type_id_sequence")
    private long roleId;

    @Column(name = "personal_code")
    @Size(max = 32)
    private String personalCode;

    @Column(name = "title")
    @Size(max = 256)
    private String title;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "jurisdiction_role_id")
    @Size(max = 64)
    private String jurisdictionRoleId;

    @Column(name = "jurisdiction_role_name_id")
    @Size(max = 64)
    private String jurisdictionRoleNameId;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_code", referencedColumnName = "personal_code",
            insertable = false, updatable = false, nullable = false)
    private UserProfile userProfile;

}
