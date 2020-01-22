package uk.gov.hmcts.reform.judicialapi.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity(name = "judicial_office_appointment")
@NoArgsConstructor
@Getter
@Setter
public class JudicialOfficeAppointment {

    @Id
    private long judicialOfficeAppointmentId;

    @ManyToOne
    @JoinColumn(name = "ELINKS_ID")
    private JudicialUserProfile judicialUserProfile;

    @ManyToOne
    @JoinColumn(name = "ROLE_ID")
    private JudicialRoleType judicialRoleType;

    @ManyToOne
    @JoinColumn(name = "CONTRACT_TYPE_ID")
    private ContractType contractType;

    @ManyToOne
    @JoinColumn(name = "BASE_LOCATION_ID")
    private BaseLocationType baseLocationType;

    @ManyToOne
    @JoinColumn(name = "REGION_ID")
    private RegionType regionType;

    @Column(name = "IS_PRINCIPLE_APPOINTMENT")
    private boolean isPrincipleAppointment;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    @Column(name = "ACTIVE_FLAG")
    private boolean activeFlag;

    @Column(name = "EXTRACTED_DATE")
    private LocalDateTime extractedDate;

    @Column(name = "CREATED_DATE")
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "LAST_LOADED_DATE")
    @UpdateTimestamp
    private LocalDateTime lastLoadedDate;

    public JudicialOfficeAppointment(long judicialOfficeAppointmentId, JudicialUserProfile judicialUserProfile,
                                     JudicialRoleType judicialRoleType, ContractType contractType, BaseLocationType baseLocationType,
                                     RegionType regionType, boolean isPrincipleAppointment, LocalDateTime startDate, LocalDateTime endDate,
                                     boolean activeFlag, LocalDateTime createdDate, LocalDateTime lastLoadedDate) {
        this.judicialOfficeAppointmentId = judicialOfficeAppointmentId;
        this.judicialUserProfile = judicialUserProfile;
        this.judicialRoleType = judicialRoleType;
        this.contractType = contractType;
        this.baseLocationType = baseLocationType;
        this.regionType = regionType;
        this.isPrincipleAppointment = isPrincipleAppointment;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activeFlag = activeFlag;
        this.createdDate = createdDate;
        this.lastLoadedDate = lastLoadedDate;
    }

}
