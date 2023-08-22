package uk.gov.hmcts.reform.judicialapi.elinks.response;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.reform.judicialapi.elinks.response.BaseLocationResponse.toBaseLocationEntity;

class BaseLocationResponseTest {

    @Test
    void testToBaseLocationEntity() {
        BaseLocationResponse baseLocationOne = new BaseLocationResponse();
        baseLocationOne.setId("1");
        baseLocationOne.setName("Aberconwy");
        baseLocationOne.setTypeId("46");
        baseLocationOne.setParentId("1722");
        baseLocationOne.setTypeId("28");
        baseLocationOne.setJurisdictionId("jurisdictionId");
        baseLocationOne.setStartDate("2021-04-12");
        baseLocationOne.setEndDate("2023-04-12");
        baseLocationOne.setCreatedAt("2023-04-12T16:42:35Z");
        baseLocationOne.setUpdatedAt("2023-04-12T16:42:35Z");

        BaseLocation baseLocation = toBaseLocationEntity(baseLocationOne);
        assertEquals(baseLocationOne.getId(),baseLocation.getBaseLocationId());
        assertEquals(baseLocationOne.getName(),baseLocation.getName());
        assertEquals(baseLocationOne.getTypeId(),baseLocation.getTypeId());
        assertEquals(baseLocationOne.getParentId(),baseLocation.getParentId());
        assertEquals(baseLocationOne.getJurisdictionId(),baseLocation.getJurisdictionId());
        assertEquals(convertToLocalDate(baseLocationOne.getEndDate()),baseLocation.getEndDate());
        assertEquals(convertToLocalDate(baseLocationOne.getStartDate()),baseLocation.getStartDate());
        assertEquals(convertToLocalDateTime(baseLocationOne.getCreatedAt()),baseLocation.getCreatedAt());
        assertEquals(convertToLocalDateTime(baseLocationOne.getUpdatedAt()),baseLocation.getUpdatedAt());

    }

    private static LocalDate convertToLocalDate(String date) {
        if (Optional.ofNullable(date).isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, formatter);
        }
        return null;
    }

    private static LocalDateTime convertToLocalDateTime(String date) {
        if (Optional.ofNullable(date).isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            return LocalDateTime.parse(date, formatter);
        }
        return null;
    }

}
