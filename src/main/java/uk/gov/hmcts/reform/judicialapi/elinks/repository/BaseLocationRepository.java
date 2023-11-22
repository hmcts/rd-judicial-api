package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;

@SuppressWarnings("all")
@Repository

public interface BaseLocationRepository extends JpaRepository<BaseLocation, String> {

        @Query(value ="select distinct parentId from BaseLocation where baseLocationId =:base_location_id ")
        String fetchParentId(String base_location_id);


}
