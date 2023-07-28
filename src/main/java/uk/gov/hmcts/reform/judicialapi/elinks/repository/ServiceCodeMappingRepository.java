package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ServiceCodeMapping;

import java.util.List;
import java.util.Set;

@Repository("elinksServiceCodeMappingRepository")
public interface ServiceCodeMappingRepository extends JpaRepository<ServiceCodeMapping, String> {

    List<ServiceCodeMapping> findByServiceCodeIgnoreCase(String serviceCode);

    @Query(value = "select jscm from serviceCodeMapping jscm")
    List<ServiceCodeMapping> findAllServiceCodeMapping();

    @Query(value = "select ticketCode from serviceCodeMapping where serviceCode IN :ccdServiceCode")
    List<String> fetchTicketCodeFromServiceCode(Set<String> ccdServiceCode);


}
