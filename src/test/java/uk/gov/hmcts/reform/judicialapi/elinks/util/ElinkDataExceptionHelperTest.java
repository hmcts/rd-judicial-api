package uk.gov.hmcts.reform.judicialapi.elinks.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;

import java.time.LocalDateTime;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
        elinkDataExceptionHelper.auditException(JUDICIAL_REF_DATA_ELINKS,
                LocalDateTime.now(),
                "ElinksApiJobScheduler" +  LocalDateTime.now(),
                "Schedular_Run_date", "JRD load failed since job has already ran for the day", "ElinksApiJobScheduler");

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
                "ElinksApiJobScheduler"));

    }
}