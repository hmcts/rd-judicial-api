package uk.gov.hmcts.reform.judicialapi.elinks.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String regionId;

    @Column(name = "hmcts_region_desc_en")
    @Size(max = 256)
    private String regionDescEn;

    @Column(name = "hmcts_region_desc_cy")
    @Size(max = 256)
    private String regionDescCy;

    @OneToMany(targetEntity = Appointment.class, mappedBy = "regionType")
    private List<Appointment> appointments;

}
