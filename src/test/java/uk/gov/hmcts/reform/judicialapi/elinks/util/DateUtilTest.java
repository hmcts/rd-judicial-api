package uk.gov.hmcts.reform.judicialapi.elinks.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateUtilTest {

    private static final String FIELD_NAME = "Start Date";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    @Test
    void shouldReturnNullWhenDateIsNull() {
        LocalDate localDate = DateUtil.convertToLocalDate(FIELD_NAME, null);
        assertNull(localDate);
    }

    @Test
    void shouldConvertToLocalDate() {
        LocalDate localDate = DateUtil.convertToLocalDate(FIELD_NAME, "2024-06-24");
        assertNotNull(localDate);
    }

    @Test
    void shouldThrowExceptionWhenInvalidDateIsPassed() {
        DateTimeParseException exception = assertThrows(DateTimeParseException.class, () ->
                DateUtil.convertToLocalDate(FIELD_NAME, "2024-14-24"));

        assertTrue(exception.getMessage().contains(FIELD_NAME));
    }

    @Test
    void shouldConvertToLocalDateTime() {
        LocalDateTime localDateTime = DateUtil.convertToLocalDateTime(FIELD_NAME,
                DATE_TIME_FORMAT,
                "2016-04-30T00:00:00Z");
        assertNotNull(localDateTime);
    }

    @Test
    void shouldThrowExceptionWhenInvalidDateTimeIsPassed() {
        DateTimeParseException exception = assertThrows(DateTimeParseException.class,
                () -> DateUtil.convertToLocalDateTime(FIELD_NAME,
                        DATE_TIME_FORMAT,
                        "2016-04-30T00"));
        assertTrue(exception.getMessage().contains(FIELD_NAME));
    }

    @Test
    void shouldReturnNullWhenDateTimeIsNull() {
        LocalDateTime dateTime = DateUtil.convertToLocalDateTime(FIELD_NAME, DATE_TIME_FORMAT, null);
        assertNull(dateTime);
    }

    @Test
    void shouldConvertToLocalDateTimeNoFormat() {
        LocalDateTime localDateTime = DateUtil.convertToLocalDateTime(FIELD_NAME, "2024-06-24");
        assertNotNull(localDateTime);
    }

    @Test
    void shouldReturnNullWhenDateIsNullNoFormat() {
        LocalDateTime localDateTime = DateUtil.convertToLocalDateTime(FIELD_NAME, null);
        assertNull(localDateTime);
    }

    @Test
    void shouldThrowExceptionWhenInvalidDateIsPassedNoFormat() {
        DateTimeParseException exception = assertThrows(DateTimeParseException.class, () ->
                DateUtil.convertToLocalDateTime(FIELD_NAME, "2024-14-24"));
        assertTrue(exception.getMessage().contains(FIELD_NAME));
    }

}