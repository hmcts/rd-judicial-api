package uk.gov.hmcts.reform.judicialapi.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "judicial_office_authorisation")
@Getter
@Setter
@NoArgsConstructor
public class Authorisation implements Serializable {

    @Id
    @Column(name = "judicial_office_auth_Id")
    private Long officeAuthId;

    @Column(name = "per_id")
    private String perId;

    @Column(name = "jurisdiction")
    @Size(max = 256)
    private String jurisdiction;

    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "lower_level")
    @Size(max = 256)
    private String lowerLevel;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "per_id", referencedColumnName = "per_id",
            insertable = false, updatable = false, nullable = false)
    private UserProfile userProfile;

    @Column(name = "personal_code")
    @Size(max = 32)
    private String personalCode;

    @Column(name = "ticket_code")
    private String ticketCode;

    @Column(name = "object_id")
    private String objectId;
}
