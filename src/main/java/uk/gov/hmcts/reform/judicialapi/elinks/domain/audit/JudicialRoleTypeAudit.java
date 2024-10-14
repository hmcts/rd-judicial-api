package uk.gov.hmcts.reform.judicialapi.elinks.domain.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity(name = "judicial_additional_roles_audit")
@Table(name = "judicial_additional_roles_audit", schema = "dbjudicialdata")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JudicialRoleTypeAudit {

    @Id
    @Column(name = "role_id")
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
}
