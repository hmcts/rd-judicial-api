package uk.gov.hmcts.reform.judicialapi.elinks.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import uk.gov.hmcts.reform.judicialapi.controller.advice.InvalidRequestException;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
class RequestUtilsTest {

    @InjectMocks
    RequestUtils requestUtils;

    @Test
    void testInvalidatePageNumber() {

        assertThrows(InvalidRequestException.class, () -> requestUtils.validateAndBuildPaginationObject(1,
            -1,"ASC", "objectId",
            20, "id", UserProfile.class));
    }

    @ParameterizedTest
    @ValueSource(ints =  {-1, 0})
    void testInvalidatePageSize(int pageSize) {

        assertThrows(InvalidRequestException.class, () -> requestUtils.validateAndBuildPaginationObject(pageSize,
            1,"ASC", "objectId",
            20, "id", UserProfile.class));
    }

    @Test
    void testInvalidateSortDirection() {

        assertThrows(InvalidRequestException.class, () -> requestUtils.validateAndBuildPaginationObject(1,
            1,"AAA", "objectId",
            20, "id", UserProfile.class));
    }

    @Test
    void testValidatePageNumber() {

        PageRequest pageRequest = requestUtils.validateAndBuildPaginationObject(1,
            null,"ASC", "objectId",
            20, "id", UserProfile.class);
        assertEquals(0,pageRequest.getPageNumber());
    }

    @Test
    void testValidateSortDirection() {

        PageRequest pageRequest = requestUtils.validateAndBuildPaginationObject(1,
            null,"", "objectId",
            20, "id", UserProfile.class);
        assertEquals("objectId: ASC",String.valueOf(pageRequest.getSort()));
    }

}