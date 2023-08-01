package uk.gov.hmcts.reform.judicialapi.elinks.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity(name = "JudicialRoleType")
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
}
