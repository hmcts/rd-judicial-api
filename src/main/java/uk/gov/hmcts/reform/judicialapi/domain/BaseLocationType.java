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

@Entity(name = "base_location_type")
@Getter
@Setter
@NoArgsConstructor
public class BaseLocationType {

    @Id
    @Column(name = "base_location_Id")
    private String baseLocationId;

    @Column(name = "court_name")
    @Size(max = 128)
    private String courtName;

    @Column(name = "bench")
    @Size(max = 128)
    private String bench;

    @Column(name = "court_type")
    @Size(max = 128)
    private String courtType;

    @Column(name = "circuit")
    @Size(max = 128)
    private String circuit;

    @Column(name = "area_of_expertise")
    @Size(max = 128)
    private String areaOfExpertise;

    @Column(name = "national_court_code")
    @Size(max = 128)
    private String nationalCourtCode;

    @OneToMany(targetEntity = Appointment.class, mappedBy = "baseLocationType")
    private List<Appointment> appointments;

}
