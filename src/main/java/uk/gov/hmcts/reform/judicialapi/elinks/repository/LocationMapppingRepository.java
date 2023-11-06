package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.LocationMapping;

@Repository
public interface LocationMapppingRepository  extends JpaRepository<LocationMapping, String> {

    @Query(value = "select distinct epimmsId from uk.gov.hmcts.reform.judicialapi.elinks.domain.LocationMapping  "
            + "lm  where judicialBaseLocationId =:locationId ")
    String fetchEpimmsIdfromLocationId(String locationId);

    @Query(value = "select distinct serviceCode from uk.gov.hmcts.reform.judicialapi.elinks.domain.LocationMapping  "
        + "lm  where judicialBaseLocationId =:locationId ")
    List<String> fetchServiceCodefromLocationId(String locationId);

}
