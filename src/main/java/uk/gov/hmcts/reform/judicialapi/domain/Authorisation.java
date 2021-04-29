package uk.gov.hmcts.reform.judicialapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "judicial_office_authorisation")
@Getter
@Setter
@NoArgsConstructor
public class Authorisation implements Serializable {

    @Id
    @Column(name = "judicial_office_auth_Id")
    private Long officeAuthId;

    @Column(name = "jurisdiction_id")
    @Size(max = 256)
    private String jurisdictionId;

    @Column(name = "authorisation_date")
    private LocalDate authorisationDate;

    @Column(name = "extracted_date")
    private LocalDate extractedDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_loaded_date")
    private LocalDateTime lastLoadedDate;

    @ManyToOne
    @JoinColumn(name = "elinks_Id", nullable = false)
    private UserProfile userProfile;

}
