package uk.gov.hmcts.reform.judicialapi.elinks.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class DateUtil {

    private DateUtil() {
    }

    public static LocalDate convertToLocalDate(String fieldName, String date) {
        if (Optional.ofNullable(date).isPresent()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDate.parse(date, formatter);
            } catch (DateTimeParseException e) {
                formatErrorMessage(fieldName, e);
            }
        }
        return null;
    }

    public static LocalDateTime convertToLocalDateTime(String fieldName, String date) {
        if (Optional.ofNullable(date).isPresent()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDate.parse(date, formatter).atStartOfDay();
            } catch (DateTimeParseException e) {
                formatErrorMessage(fieldName, e);
            }
        }
        return null;
    }

    public static LocalDateTime convertToLocalDateTime(String fieldName,
                                                       String datePattern,
                                                       String date) {
        if (Optional.ofNullable(date).isPresent()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
                return LocalDateTime.parse(date, formatter);
            } catch (DateTimeParseException e) {
                formatErrorMessage(fieldName, e);
            }
        }
        return null;
    }

    private static void formatErrorMessage(String fieldName, DateTimeParseException e) {
        String errorMessage = "Error Field: %s %s";
        throw new DateTimeParseException(String.format(errorMessage, fieldName, e.getMessage()),
                e.getParsedString(),
                e.getErrorIndex(), e);
    }
}
