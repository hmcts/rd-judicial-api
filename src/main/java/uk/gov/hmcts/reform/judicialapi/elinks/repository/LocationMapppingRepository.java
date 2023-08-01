package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.LocationMapping;

public interface LocationMapppingRepository  extends JpaRepository<LocationMapping, String> {

    @Query(value = "select epimmsId from uk.gov.hmcts.reform.judicialapi.elinks.domain.LocationMapping  "
            + "lm  where judicialBaseLocationId =:locationId")
    String fetchEpimmsIdfromLocationId(String locationId);

}
