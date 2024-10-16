package uk.gov.hmcts.reform.judicialapi.elinks.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "serviceCodeMapping")
@Table(name = "judicial_service_code_mapping", schema = "dbjudicialdata")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCodeMapping {
    @Id
    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "ticket_code")
    private String ticketCode;

    @Column(name = "service_code")
    private String serviceCode;

    @Column(name = "service_description")
    private String serviceDescription;
}
