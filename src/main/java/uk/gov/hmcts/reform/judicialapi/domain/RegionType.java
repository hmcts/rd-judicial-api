package uk.gov.hmcts.reform.judicialapi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity(name = "region_type")
@Getter
@Setter
@NoArgsConstructor
public class RegionType implements Serializable {

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
