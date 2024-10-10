package uk.gov.hmcts.reform.judicialapi.elinks.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataExceptionRecords;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;

@ExtendWith(MockitoExtension.class)
class ElinkDataExceptionHelperTest {

    @InjectMocks
    ElinkDataExceptionHelper elinkDataExceptionHelper;

    @Spy
    ElinkDataExceptionRepository elinkDataExceptionRepository;

    @Test
    void auditExceptionSuccess() {

        ElinkDataExceptionRecords audit = spy(ElinkDataExceptionRecords.class);
        elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                LocalDateTime.now(),
                "ElinksApiJobScheduler" +  LocalDateTime.now(),
                "Schedular_Run_date", "JRD load failed since job has already ran for the day",
            "ElinksApiJobScheduler", null,1);

        verify(elinkDataExceptionRepository, times(1))
                .save(any());

    }

    @Test
    void auditExceptionSuccessWithEmptyMessage() {

        elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                LocalDateTime.now(),
                "ElinksApiJobScheduler" +  LocalDateTime.now(),
                "Schedular_Run_date", "JRD load failed since job has already ran for the day",
                "ElinksApiJobScheduler", null, 1, "");

        verify(elinkDataExceptionRepository, times(1))
                .save(any());

    }

    @Test
    void auditExceptionSuccessWithNullMessage() {

        elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                LocalDateTime.now(),
                "ElinksApiJobScheduler" +  LocalDateTime.now(),
                "Schedular_Run_date", "JRD load failed since job has already ran for the day",
                "ElinksApiJobScheduler", null, 1, null);

        verify(elinkDataExceptionRepository, times(1))
                .save(any());

    }

    @Test
    void auditExceptionFailure() {
        when(elinkDataExceptionRepository.save(any())).thenThrow(new RuntimeException("Some Exception"));
        assertThrows(Exception.class, () -> elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                LocalDateTime.now(),
                "ElinksApiJobScheduler" +  LocalDateTime.now(),
                "Schedular_Run_date", "JRD load failed since job has already ran for the day",
                "ElinksApiJobScheduler",null,1));

    }


    @Test
    void auditExceptionListSuccess() {

        String personalCode1 = "123";
        String personalCode2 = "234";

        List<String> personalCodes = new ArrayList<>();
        personalCodes.add(personalCode1);
        personalCodes.add(personalCode2);

        elinkDataExceptionHelper.auditException(personalCodes, LocalDateTime.now());

        verify(elinkDataExceptionRepository, times(1)).saveAll(any());

    }


    @Test
    void auditExceptionListFailure() {
        when(elinkDataExceptionRepository.saveAll(any())).thenThrow(new RuntimeException("Some Exception"));
        String personalCode1 = "123";
        String personalCode2 = "234";

        List<String> personalCodes = new ArrayList<>();
        personalCodes.add(personalCode1);
        personalCodes.add(personalCode2);
        assertThrows(Exception.class, () -> elinkDataExceptionHelper.auditException(personalCodes,
                LocalDateTime.now()
        ));

    }



}