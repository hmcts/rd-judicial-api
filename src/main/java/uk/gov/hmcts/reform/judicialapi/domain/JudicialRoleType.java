package uk.gov.hmcts.reform.judicialapi.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;


@Entity(name = "judicial_role_type")
@Getter
@Setter
@NoArgsConstructor
public class JudicialRoleType implements Serializable {

    @Id
    @Column(name = "role_id")
    private String roleId;

    @Column(name = "per_id")
    private String perId;

    @Column(name = "title")
    private String title;

    @Column(name = "location")
    private String location;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "per_id", referencedColumnName = "per_id",
            insertable = false, updatable = false, nullable = false)
    private UserProfile userProfile;

}
