package uk.gov.hmcts.reform.judicialapi.controller.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.util.RequestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RequestUtilsTest {

    @Test
    public void testValidateAndBuildPaginationObject() {
        var pageRequest =
                RequestUtils.validateAndBuildPaginationObject(1, 0,
                        "ASC", "objectId",
                        20, "id", UserProfile.class);
        assertEquals(0, pageRequest.first().getPageNumber());
        assertEquals(1, pageRequest.first().getPageSize());
    }

    @Test
    public void testInvalidRequestExceptionForInvalidPageNumber() {
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            RequestUtils.validateAndBuildPaginationObject(-1, 1,
                    "ASC", "objectId",
                    20, "id", UserProfile.class);
        });
    }

    @Test
    public void testInvalidRequestExceptionForInvalidPageSize() {
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            RequestUtils.validateAndBuildPaginationObject(0, -1,
                    "ASC", "objectId",
                    20, "id", UserProfile.class);
        });
    }

    @Test
    public void testInvalidRequestExceptionForInvalidSortDirection() {
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            RequestUtils.validateAndBuildPaginationObject(0, 1,
                    "ASC", "Invalid",
                    20, "id", UserProfile.class);
        });
    }

    @Test
    public void testConfigValueWhenPaginationParametersNotProvided() {
        var pageRequest =
                RequestUtils.validateAndBuildPaginationObject(null, null,
                        null, null,
                        20, "objectId", UserProfile.class);
        assertEquals(0, pageRequest.getPageNumber());
        assertEquals(20, pageRequest.getPageSize());
        assertTrue(pageRequest.getSort().get().anyMatch(i -> i.getDirection().isAscending()));
        assertTrue(pageRequest.getSort().get().anyMatch(i -> i.getProperty().equals("objectId")));
    }

    @Test
    public void testInvalidRequestExceptionForInvalidSortColumn() {
        Assertions.assertThrows(InvalidRequestException.class, () -> {
            RequestUtils.validateAndBuildPaginationObject(0, 1,
                    "invalid", "objectId",
                    20, "invalid", UserProfile.class);
        });
    }
}
