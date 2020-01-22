package uk.gov.hmcts.reform.judicialapi.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Entity(name = "judicial_role_type")
@NoArgsConstructor
@Getter
@Component
public class JudicialRoleType {

    @Id
    @Size(max = 64)
    private String roleId;

    @Column(name = "ROLE_DESC_EN")
    @Size(max = 256)
    private String roleDescEn;

    @Column(name = "ROLE_DESC_CY")
    @Size(max = 256)
    private String roleDescCy;

    @OneToMany(mappedBy = "judicialRoleType")
    private List<JudicialOfficeAppointment> judicialOfficeAppointments = new ArrayList<>();

    public JudicialRoleType(String roleId, String roleDescEn, String roleDescCy) {
        this.roleId = roleId;
        this.roleDescEn = roleDescEn;
        this.roleDescCy = roleDescCy;
    }

}
