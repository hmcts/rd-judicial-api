package uk.gov.hmcts.reform.judicialapi.elinks.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public final class DateUtil {

    public static LocalDate convertToLocalDate(String fieldName, String date) {
        if (Optional.ofNullable(date).isPresent()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDate.parse(date, formatter);
            } catch (DateTimeParseException e) {
                String errorMessage = "Error Field: %s %s";
                throw new DateTimeParseException(String.format(errorMessage, fieldName, e.getMessage()),
                        e.getParsedString(),
                        e.getErrorIndex(), e);
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
                String errorMessage = "Error Field: %s %s";
                throw new DateTimeParseException(String.format(errorMessage, fieldName, e.getMessage()),
                        e.getParsedString(),
                        e.getErrorIndex(), e);
            }
        }
        return null;
    }
}
