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

@Entity(name = "authorisation_type")
@Getter
@Setter
@NoArgsConstructor
public class AuthorisationType {

    @Id
    @Column(name = "authorisation_Id")
    @Size(max = 64)
    private String authorisationId;

    @Column(name = "authorisation_desc_en")
    @Size(max = 256)
    private String authorisationDescEen;

    @Column(name = "authorisation_desc_cy")
    @Size(max = 256)
    private String authorisationDescCy;

    @Column(name = "jurisdiction_Id")
    @Size(max = 64)
    private String jurisdictionId;

    @Column(name = "jurisdiction_desc_en")
    @Size(max = 256)
    private String jurisdictionDescEn;

    @Column(name = "jurisdiction_desc_cy")
    @Size(max = 256)
    private String jurisdictionDescCy;

    @OneToMany(targetEntity = Authorisation.class, mappedBy = "authorisationType")
    private List<Authorisation> authorisations;

}
