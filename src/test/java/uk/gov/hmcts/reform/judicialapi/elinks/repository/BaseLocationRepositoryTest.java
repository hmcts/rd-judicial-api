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
        assertThat(result.getCourtType()).isEqualTo(baseLocationOne.getCourtType());
        assertThat(result.getCircuit()).isEqualTo(baseLocationOne.getCircuit());
        assertThat(result.getAreaOfExpertise()).isEqualTo(baseLocationOne.getAreaOfExpertise());

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
        assertThat(result.get(0).getCourtType()).isEqualTo(baseLocationOne.getCourtType());
        assertThat(result.get(0).getCircuit()).isEqualTo(baseLocationOne.getCircuit());
        assertThat(result.get(0).getAreaOfExpertise()).isEqualTo(baseLocationOne.getAreaOfExpertise());

        assertThat(result.get(1).getBaseLocationId()).isEqualTo(baseLocationTwo.getBaseLocationId());
        assertThat(result.get(1).getCourtName()).isEqualTo(baseLocationTwo.getCourtName());
        assertThat(result.get(1).getCourtType()).isEqualTo(baseLocationTwo.getCourtType());
        assertThat(result.get(1).getCircuit()).isEqualTo(baseLocationTwo.getCircuit());
        assertThat(result.get(1).getAreaOfExpertise()).isEqualTo(baseLocationTwo.getAreaOfExpertise());
    }



    private List<BaseLocation> getBaseLocationEntityList() {


        BaseLocation baseLocationOne = new BaseLocation();
        baseLocationOne.setBaseLocationId("1");
        baseLocationOne.setCourtName("National");
        baseLocationOne.setCourtType("Old Gwynedd");
        baseLocationOne.setCircuit("Gwynedd");
        baseLocationOne.setAreaOfExpertise("LJA");


        BaseLocation baseLocationTwo = new BaseLocation();
        baseLocationTwo.setBaseLocationId("2");
        baseLocationTwo.setCourtName("Aldridge and Brownhills");
        baseLocationTwo.setCourtType("Nottinghamshire");
        baseLocationTwo.setCircuit("Nottinghamshire");
        baseLocationTwo.setAreaOfExpertise("LJA");



        List<BaseLocation> baseLocations = new ArrayList<>();

        baseLocations.add(baseLocationOne);
        baseLocations.add(baseLocationTwo);

        return baseLocations;

    }
}
