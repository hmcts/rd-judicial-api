package uk.gov.hmcts.reform.judicialapi.elinks.repository;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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
        assertThat(result.getName()).isEqualTo(baseLocationOne.getName());
        assertThat(result.getTypeId()).isEqualTo(baseLocationOne.getTypeId());
        assertThat(result.getParentId()).isEqualTo(baseLocationOne.getParentId());
        assertThat(result.getJurisdictionId()).isEqualTo(baseLocationOne.getJurisdictionId());

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
        assertThat(result.get(0).getName()).isEqualTo(baseLocationOne.getName());
        assertThat(result.get(0).getTypeId()).isEqualTo(baseLocationOne.getTypeId());
        assertThat(result.get(0).getParentId()).isEqualTo(baseLocationOne.getParentId());
        assertThat(result.get(0).getJurisdictionId()).isEqualTo(baseLocationOne.getJurisdictionId());

        assertThat(result.get(1).getBaseLocationId()).isEqualTo(baseLocationTwo.getBaseLocationId());
        assertThat(result.get(1).getName()).isEqualTo(baseLocationTwo.getName());
        assertThat(result.get(1).getTypeId()).isEqualTo(baseLocationTwo.getTypeId());
        assertThat(result.get(1).getParentId()).isEqualTo(baseLocationTwo.getParentId());
        assertThat(result.get(1).getJurisdictionId()).isEqualTo(baseLocationTwo.getJurisdictionId());
    }



    private List<BaseLocation> getBaseLocationEntityList() {


        BaseLocation baseLocationOne = new BaseLocation();
        baseLocationOne.setBaseLocationId("1");
        baseLocationOne.setName("National");
        baseLocationOne.setTypeId("46");
        baseLocationOne.setParentId("1722");
        baseLocationOne.setJurisdictionId("28");
        baseLocationOne.setStartDate(null);
        baseLocationOne.setCreatedAt(convertToLocalDateTime("2023-04-12T16:42:35Z"));
        baseLocationOne.setUpdatedAt(convertToLocalDateTime("2023-04-12T16:42:35Z"));


        BaseLocation baseLocationTwo = new BaseLocation();
        baseLocationTwo.setBaseLocationId("3");
        baseLocationTwo.setName("Alnwick");
        baseLocationTwo.setTypeId("46");
        baseLocationTwo.setParentId("1722");
        baseLocationTwo.setJurisdictionId("28");
        baseLocationTwo.setStartDate(null);
        baseLocationTwo.setCreatedAt(convertToLocalDateTime("2023-04-12T16:42:35Z"));
        baseLocationTwo.setUpdatedAt(convertToLocalDateTime("2023-04-12T16:42:35Z"));



        return List.of(baseLocationOne,baseLocationTwo);

    }

    private  LocalDateTime convertToLocalDateTime(String date) {
        if (Optional.ofNullable(date).isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            return LocalDate.parse(date, formatter).atStartOfDay();
        }
        return null;
    }
}
