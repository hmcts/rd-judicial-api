package uk.gov.hmcts.reform.judicialapi.elinks.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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


    @Column(name = "service_code")
    @Size(max = 16)
    private String serviceCode;

}
