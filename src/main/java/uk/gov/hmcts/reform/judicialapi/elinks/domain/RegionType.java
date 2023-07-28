package uk.gov.hmcts.reform.judicialapi.elinks.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.List;

@Entity(name = "hmcts_region_type")
@Table(name = "hmcts_region_type", schema = "dbjudicialdata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionType {

    @Id
    @Column(name = "hmcts_region_id")
    @Size(max = 64)
    private String RegionId;

    @Column(name = "hmcts_region_desc_en")
    @Size(max = 256)
    private String RegionDescEn;

    @Column(name = "hmcts_region_desc_cy")
    @Size(max = 256)
    private String RegionDescCy;

    @OneToMany(targetEntity = Appointment.class, mappedBy = "regionType")
    private List<Appointment> appointments;

}
