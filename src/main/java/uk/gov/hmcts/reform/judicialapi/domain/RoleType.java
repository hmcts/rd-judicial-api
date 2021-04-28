package uk.gov.hmcts.reform.judicialapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity(name = "judicial_role_type")
@Getter
@Setter
@NoArgsConstructor
public class RoleType {

    @Id
    @Column(name = "role_Id")
    @Size(max = 64)
    private String role_id;

    @Column(name = "role_desc_en")
    @Size(max = 256)
    private String roleDescEn;

    @Column(name = "role_desc_cy")
    @Size(max = 256)
    private String roleDescCy;

}
