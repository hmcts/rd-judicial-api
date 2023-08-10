package uk.gov.hmcts.reform.judicialapi.elinks.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;

@ExtendWith(MockitoExtension.class)
class CommonUtilTest {

    @InjectMocks
    CommonUtil commonUtil;

    @Test
    void getUpdatedDateFormatTest() {

        String lastupdated=commonUtil.getUpdatedDateFormat("2015-01-01");
        assertNotNull(convertToLocalDate(lastupdated));
    }

    @Test
    void getUpdatedDateFormatTestInvalidDate() {

        String lastupdated=commonUtil.getUpdatedDateFormat("Wed Oct 16 00:00:00 CEST 2013");
        assertNotNull(convertToLocalDate(lastupdated));
    }

    private static LocalDate convertToLocalDate(String date) {
        if (Optional.ofNullable(date).isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, formatter);
        }
        return null;
    }
}