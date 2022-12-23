package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.LocationMapping;

public interface LocationMapppingRepository  extends JpaRepository<LocationMapping, String> {
}
