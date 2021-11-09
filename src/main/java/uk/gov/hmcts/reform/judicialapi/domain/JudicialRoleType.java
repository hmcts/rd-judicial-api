package uk.gov.hmcts.reform.judicialapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;
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
