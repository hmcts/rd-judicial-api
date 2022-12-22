package uk.gov.hmcts.reform.judicialapi.elinks.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity(name = "BaseLocation")
@Table(name = "base_location_type", schema = "dbjudicialdata")
@Getter
@Setter
@NoArgsConstructor
public class BaseLocation {

    @Id
    @Column(name = "base_location_Id")
    @JsonProperty(value = "id")
    private String baseLocationId;

    @Column(name = "court_name")
    @Size(max = 128)
    @JsonProperty(value = "name")
    private String courtName;

    @Column(name = "court_type")
    @Size(max = 128)
    @JsonProperty(value = "orgunit2name")
    private String courtType;

    @Column(name = "circuit")
    @Size(max = 128)
    @JsonProperty(value = "orgunit3name")
    private String circuit;

    @Column(name = "area_of_expertise")
    @Size(max = 128)
    @JsonProperty(value = "orgunit4name")
    private String areaOfExpertise;

}
