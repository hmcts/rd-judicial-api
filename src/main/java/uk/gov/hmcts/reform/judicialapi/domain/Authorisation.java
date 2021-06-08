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
import java.time.LocalDateTime;

@Entity(name = "judicial_office_authorisation")
@Getter
@Setter
@NoArgsConstructor
public class Authorisation implements Serializable {

    @Id
    @Column(name = "judicial_office_auth_Id")
    private Long officeAuthId;

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

    @ManyToOne
    @JoinColumn(name = "elinks_Id", nullable = false)
    private UserProfile userProfile;

}
