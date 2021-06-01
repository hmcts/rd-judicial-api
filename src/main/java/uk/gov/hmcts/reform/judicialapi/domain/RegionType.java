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

@Entity(name = "region_type")
@Getter
@Setter
@NoArgsConstructor
public class RegionType {

    @Id
    @Column(name = "region_Id")
    private String regionId;

    @Column(name = "region_desc_en")
    @Size(max = 256)
    private String regionDescEn;

    @Column(name = "region_desc_cy")
    @Size(max = 256)
    private String regionDescCy;

    @OneToMany(targetEntity = Appointment.class, mappedBy = "regionType")
    private List<Appointment> appointments;

}
