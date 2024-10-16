package uk.gov.hmcts.reform.judicialapi.elinks.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;



@Entity(name = "judicialOfficeAuthorisation")
@Table(name = "judicial_office_authorisation", schema = "dbjudicialdata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "judicial_office_auth_id_sequence",
        sequenceName = "judicial_office_auth_id_sequence", schema = "dbjudicialdata", allocationSize = 1)
public class Authorisation implements Serializable {

    @Id
    @Column(name = "judicial_office_auth_Id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "judicial_office_auth_id_sequence")
    private Long officeAuthId;

    @Column(name = "personal_code")
    @Size(max = 32)
    private String personalCode;

    @Column(name = "jurisdiction")
    @Size(max = 256)
    private String jurisdiction;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "lower_level")
    @Size(max = 256)
    private String lowerLevel;

    @Column(name = "ticket_code")
    @Size(max = 16)
    private String ticketCode;

    @Column(name = "appointment_id")
    @Size(max = 256)
    private String appointmentId;

    @Column(name = "authorisation_id")
    @Size(max = 256)
    private String authorisationId;

    @Column(name = "jurisdiction_id")
    @Size(max = 64)
    private String jurisdictionId;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_code", referencedColumnName = "personal_code",
            insertable = false, updatable = false, nullable = false)
    private UserProfile userProfile;


}
