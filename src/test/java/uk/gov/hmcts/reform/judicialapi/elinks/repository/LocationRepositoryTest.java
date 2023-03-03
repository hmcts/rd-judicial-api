package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Location;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationRepositoryTest {

    @Spy
    private LocationRepository locationRepository;



    @Test
    void test_save_location() {
        Location location = new Location();
        location.setRegionId("2");
        location.setRegionDescEn("National England and Wales");


        when(locationRepository.save(any())).thenReturn(location);

        Location result = locationRepository.save(location);

        assertThat(result.getRegionId()).isEqualTo(location.getRegionId());
        assertThat(result.getRegionDescEn()).isEqualTo(location.getRegionDescEn());
        assertThat(result.getRegionDescCy()).isBlank();
    }

    @Test
    void test_save_All_Locations() {

        Location locationOne = new Location();
        locationOne.setRegionId("1");
        locationOne.setRegionDescEn("National");

        Location locationTwo = new Location();
        locationTwo.setRegionId("2");
        locationTwo.setRegionDescEn("National England and Wales");


        Location locationThree = new Location();
        locationThree.setRegionId("3");
        locationThree.setRegionDescEn("Taylor House (London)");


        List<Location> locations = List.of(locationOne,locationTwo,locationThree);

        when(locationRepository.saveAll(anyList())).thenReturn(locations);



        List<Location> result = locationRepository.saveAll(locations);

        assertThat(result).hasSize(3);

        assertThat(result.get(0).getRegionId()).isEqualTo(locationOne.getRegionId());
        assertThat(result.get(0).getRegionDescEn()).isEqualTo(locationOne.getRegionDescEn());
        assertThat(result.get(0).getRegionDescCy()).isBlank();

        assertThat(result.get(2).getRegionId()).isEqualTo(locationThree.getRegionId());
        assertThat(result.get(2).getRegionDescEn()).isEqualTo(locationThree.getRegionDescEn());
        assertThat(result.get(2).getRegionDescCy()).isBlank();
    }

}

