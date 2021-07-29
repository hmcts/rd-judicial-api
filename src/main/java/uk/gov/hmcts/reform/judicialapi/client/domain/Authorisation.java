package uk.gov.hmcts.reform.judicialapi.client.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Authorisation implements Serializable {

    private Long officeAuthId;

    private String jurisdiction;

    private Long ticketId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime createdDate;

    private LocalDateTime lastUpdated;

    private String lowerLevel;

    @JsonIgnore
    private UserProfile userProfile;

    private String personalCode;

    private String serviceCode;

    @JsonIgnore
    private String perId;
}
