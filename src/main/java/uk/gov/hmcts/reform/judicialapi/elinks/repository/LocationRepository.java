package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, String> {

    Location findByRegionDescEnIgnoreCase(String personalCode);

    @Query(value = "select distinct regionId from Location where regionDescEn =:location ")
    String fetchRegionIdfromCftRegionDescEn(String location);
}
