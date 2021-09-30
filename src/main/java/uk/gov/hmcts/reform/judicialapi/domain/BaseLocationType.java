package uk.gov.hmcts.reform.judicialapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;

@Entity(name = "base_location_type")
@Getter
@Setter
@NoArgsConstructor
public class BaseLocationType implements Serializable {

    @Id
    @Column(name = "base_location_Id")
    private String baseLocationId;

    @Column(name = "court_name")
    @Size(max = 128)
    private String courtName;

    @Column(name = "court_type")
    @Size(max = 128)
    private String courtType;

    @Column(name = "circuit")
    @Size(max = 128)
    private String circuit;

    @Column(name = "area_of_expertise")
    @Size(max = 128)
    private String areaOfExpertise;

    @OneToMany(targetEntity = Appointment.class, mappedBy = "baseLocationType")
    private List<Appointment> appointments;
}
