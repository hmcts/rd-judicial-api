package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JrdRegionMapping;

@Repository
public interface JrdRegionMappingRepository extends JpaRepository<JrdRegionMapping, String> {

    @Query(value = "select distinct regionId from jrdlrdregionmapping where jrdRegion =:location ")
    String fetchRegionIdfromRegion(String location);
}
