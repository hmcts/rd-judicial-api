package uk.gov.hmcts.reform.judicialapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;
import java.util.List;

@Entity(name = "judicial_role_type")
@Getter
@Setter
@NoArgsConstructor
public class RoleType {

    @Id
    @Column(name = "role_Id")
    private String roleId;

    @Column(name = "role_desc_en")
    @Size(max = 256)
    private String roleDescEn;

    @Column(name = "role_desc_cy")
    @Size(max = 256)
    private String roleDescCy;

    @OneToMany(targetEntity = Appointment.class, mappedBy = "roleType")
    private List<Appointment> appointments;

}
