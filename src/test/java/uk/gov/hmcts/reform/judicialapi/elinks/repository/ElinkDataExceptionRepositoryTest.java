package uk.gov.hmcts.reform.judicialapi.elinks.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataExceptionRecords;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElinkDataExceptionRepositoryTest {

    @Spy
    private ElinkDataExceptionRepository elinkDataExceptionRepository;

    @Test
    void test_save_exception() {

        ElinkDataExceptionRecords record = new ElinkDataExceptionRecords();
        record.setId(1L);
        record.setErrorDescription("shedularFailied");


        when(elinkDataExceptionRepository.save(any())).thenReturn(record);

        ElinkDataExceptionRecords result = elinkDataExceptionRepository.save(record);

        assertThat(result.getId()).isEqualTo(record.getId());
        assertThat(result.getErrorDescription()).isEqualTo(record.getErrorDescription());
    }

    @Test
    void test_save_all_exceptions() {

        ElinkDataExceptionRecords record = new ElinkDataExceptionRecords();
        record.setId(1L);
        record.setErrorDescription("shedularFailied");

        ElinkDataExceptionRecords recordTwo = new ElinkDataExceptionRecords();
        record.setId(2L);
        record.setErrorDescription("shedularFailied");


        ElinkDataExceptionRecords recordThree = new ElinkDataExceptionRecords();
        record.setId(3L);
        record.setErrorDescription("shedularFailied");


        List<ElinkDataExceptionRecords> exceptions = List.of(record,recordTwo,recordThree);

        when(elinkDataExceptionRepository.saveAll(anyList())).thenReturn(exceptions);



        List<ElinkDataExceptionRecords> result = elinkDataExceptionRepository.saveAll(exceptions);

        assertThat(result).hasSize(3);

        assertThat(result.get(0).getId()).isEqualTo(record.getId());
        assertThat(result.get(0).getErrorDescription()).isEqualTo(record.getErrorDescription());

        assertThat(result.get(2).getId()).isEqualTo(recordThree.getId());
        assertThat(result.get(2).getErrorDescription()).isEqualTo(recordThree.getErrorDescription());
    }

}