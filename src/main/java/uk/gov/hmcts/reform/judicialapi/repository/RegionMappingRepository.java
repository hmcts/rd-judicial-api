package uk.gov.hmcts.reform.judicialapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import uk.gov.hmcts.reform.judicialapi.domain.RegionMapping;
import org.springframework.data.jpa.repository.Query;

public interface RegionMappingRepository extends JpaRepository<RegionMapping, String> {

    @Query(value = "select jlrm from jrd_lrd_region_mapping jlrm")
    List<RegionMapping> findAllRegionMappingData();
}
