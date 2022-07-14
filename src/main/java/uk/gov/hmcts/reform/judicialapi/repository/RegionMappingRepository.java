package uk.gov.hmcts.reform.judicialapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.hmcts.reform.judicialapi.domain.RegionMapping;

import java.util.List;

public interface RegionMappingRepository extends JpaRepository<RegionMapping, String> {

    @Query(value = "select jlrm from jrd_lrd_region_mapping jlrm")
    List<RegionMapping> findAllRegionMappingData();
}
