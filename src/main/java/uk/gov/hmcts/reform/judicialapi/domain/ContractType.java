package uk.gov.hmcts.reform.judicialapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity(name = "contract_type")
@Getter
@Setter
@NoArgsConstructor
public class ContractType {

    @Id
    @Column(name = "contract_type_Id")
    @Size(max = 64)
    private String contractTypeId;

    @Column(name = "contract_type_desc_en")
    @Size(max = 256)
    private String contractTypeDescEn;

    @Column(name = "contract_type_desc_cy")
    @Size(max = 256)
    private String contractTypeDescCy;

}
