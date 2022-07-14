package uk.gov.hmcts.reform.judicialapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.hmcts.reform.judicialapi.domain.ServiceCodeMapping;

import java.util.List;
import java.util.Set;

public interface ServiceCodeMappingRepository extends JpaRepository<ServiceCodeMapping, String> {

    List<ServiceCodeMapping> findByServiceCodeIgnoreCase(String serviceCode);

    @Query(value = "select jscm from judicial_service_code_mapping jscm")
    List<ServiceCodeMapping> findAllServiceCodeMapping();

    @Query(value = "select ticketCode from judicial_service_code_mapping where serviceCode IN :ccdServiceCode")
    List<String> fetchTicketCodeFromServiceCode(Set<String> ccdServiceCode);

}
