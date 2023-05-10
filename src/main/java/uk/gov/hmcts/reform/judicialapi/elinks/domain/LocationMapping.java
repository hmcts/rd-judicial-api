package uk.gov.hmcts.reform.judicialapi.elinks.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity(name = "judicialLocationMapping")
@Table(name = "judicial_location_mapping", schema = "dbjudicialdata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationMapping {


    @Column(name = "epimms_id")
    @Size(max = 16)
    private String epimmsId;

    @Column(name = "judicial_base_location_id")
    @Size(max = 64)
    @Id
    private String judicialBaseLocationId;

    @Column(name = "building_location_name")
    @Size(max = 256)
    private String buildingLocationName;

    @Column(name = "base_location_name")
    @Size(max = 128)
    private String baseLocationName;

    @Column(name = "service_code")
    @Size(max = 16)
    private String serviceCode;

}
