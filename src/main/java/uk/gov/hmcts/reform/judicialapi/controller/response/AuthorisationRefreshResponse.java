package uk.gov.hmcts.reform.judicialapi.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorisationRefreshResponse implements Serializable {

    private Long officeAuthId;

    private String jurisdiction;

    private Long ticketId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime createdDate;

    private LocalDateTime lastUpdated;

    private String lowerLevel;

    @JsonIgnore
    private UserProfileRefreshResponse userProfile;

    private String personalCode;

    private String serviceCode;

    @JsonIgnore
    private String perId;
}
