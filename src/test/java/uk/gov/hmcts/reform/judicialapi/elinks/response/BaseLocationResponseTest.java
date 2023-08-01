package uk.gov.hmcts.reform.judicialapi.elinks.response;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;

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
        baseLocationOne.setCreatedAt("2023-04-12T16:42:35Z");
        baseLocationOne.setUpdatedAt("2023-04-12T16:42:35Z");

        BaseLocation baseLocation = toBaseLocationEntity(baseLocationOne);
        assertEquals(baseLocationOne.getId(),baseLocation.getBaseLocationId());
        assertEquals(baseLocationOne.getName(),baseLocation.getName());

    }

}
