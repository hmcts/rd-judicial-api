package uk.gov.hmcts.reform.judicialapi.elinks.domain.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;


@Entity(name = "judicialOfficeAuthorisationAudit")
@Table(name = "judicial_office_authorisation_audit", schema = "dbjudicialdata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authorisation implements Serializable {

    @Id
    @Column(name = "judicial_office_auth_Id")
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
}