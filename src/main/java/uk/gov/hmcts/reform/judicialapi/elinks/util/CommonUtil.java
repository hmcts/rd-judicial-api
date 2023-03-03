package uk.gov.hmcts.reform.judicialapi.elinks.util;

import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class CommonUtil {


    public String getUpdatedDateFormat(String lastUpdated) {

        char c = lastUpdated.charAt(0);
        if (Character.isAlphabetic(c)) {
            return ZonedDateTime.parse(lastUpdated,
                            DateTimeFormatter.ofPattern(
                                    "EEE MMM dd HH:mm:ss z uuuu",
                                    Locale.UK))
                    .toLocalDate()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        return lastUpdated;
    }
}
