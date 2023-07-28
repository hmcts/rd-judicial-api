package uk.gov.hmcts.reform.judicialapi.elinks.repository;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseLocationRepositoryTest {


    @Spy
    BaseLocationRepository baseLocationRepository;


    @Test
    void test_save_baseLocation() {
        BaseLocation baseLocationOne = getBaseLocationEntityList().get(0);

        when(baseLocationRepository.save(any())).thenReturn(baseLocationOne);


        BaseLocation result = baseLocationRepository.save(baseLocationOne);

        assertThat(result.getBaseLocationId()).isEqualTo(baseLocationOne.getBaseLocationId());
        assertThat(result.getCourtName()).isEqualTo(baseLocationOne.getCourtName());
        assertThat(result.getJurisdictionId()).isEqualTo(baseLocationOne.getJurisdictionId());
        assertThat(result.getParentId()).isEqualTo(baseLocationOne.getParentId());
        assertThat(result.getTypeId()).isEqualTo(baseLocationOne.getTypeId());

    }

    @Test
    void test_save_All_BaseLocations() {

        BaseLocation baseLocationOne = getBaseLocationEntityList().get(0);
        BaseLocation baseLocationTwo = getBaseLocationEntityList().get(1);


        List<BaseLocation> baseLocations = List.of(baseLocationOne,baseLocationTwo);

        when(baseLocationRepository.saveAll(anyList())).thenReturn(baseLocations);



        List<BaseLocation> result = baseLocationRepository.saveAll(baseLocations);

        assertThat(result).hasSize(2);

        assertThat(result.get(0).getBaseLocationId()).isEqualTo(baseLocationOne.getBaseLocationId());
        assertThat(result.get(0).getCourtName()).isEqualTo(baseLocationOne.getCourtName());
        assertThat(result.get(0).getTypeId()).isEqualTo(baseLocationOne.getTypeId());
        assertThat(result.get(0).getJurisdictionId()).isEqualTo(baseLocationOne.getJurisdictionId());
        assertThat(result.get(0).getParentId()).isEqualTo(baseLocationOne.getParentId());

        assertThat(result.get(1).getBaseLocationId()).isEqualTo(baseLocationTwo.getBaseLocationId());
        assertThat(result.get(1).getCourtName()).isEqualTo(baseLocationTwo.getCourtName());
        assertThat(result.get(1).getParentId()).isEqualTo(baseLocationTwo.getParentId());
        assertThat(result.get(1).getJurisdictionId()).isEqualTo(baseLocationTwo.getJurisdictionId());
        assertThat(result.get(1).getTypeId()).isEqualTo(baseLocationTwo.getTypeId());
    }



    private List<BaseLocation> getBaseLocationEntityList() {


        BaseLocation baseLocationOne = new BaseLocation();
        baseLocationOne.setBaseLocationId("1");
        baseLocationOne.setCourtName("National");
        baseLocationOne.setJurisdictionId("Old Gwynedd");
        baseLocationOne.setTypeId("Gwynedd");
        baseLocationOne.setParentId("LJA");


        BaseLocation baseLocationTwo = new BaseLocation();
        baseLocationTwo.setBaseLocationId("2");
        baseLocationTwo.setCourtName("Aldridge and Brownhills");
        baseLocationTwo.setJurisdictionId("Nottinghamshire");
        baseLocationTwo.setTypeId("Nottinghamshire");
        baseLocationTwo.setParentId("LJA");



        List<BaseLocation> baseLocations = new ArrayList<>();

        baseLocations.add(baseLocationOne);
        baseLocations.add(baseLocationTwo);

        return baseLocations;

    }
}
