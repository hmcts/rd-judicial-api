package uk.gov.hmcts.reform.judicialapi.controller.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import uk.gov.hmcts.reform.judicialapi.controller.TestSupport;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataUtil.createPageableObject;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataUtil.distinctByKeys;

class RefDataUtilTest {

    @Test
    void testPaginationObject() {
        final Pageable page = createPageableObject(0, 10, 10);
        assertEquals(0, page.first().getPageNumber());
        assertEquals(10, page.first().getPageSize());
    }

    @Test
    void testPaginationObjectWhenSizeNotProvided() {
        final Pageable page = createPageableObject(0, null, 10);
        assertEquals(0, page.first().getPageNumber());
        assertEquals(10, page.first().getPageSize());
    }

    @Test
    void testPaginationObjectWhenPageNotProvided() {
        final Pageable page = createPageableObject(null, 10, 10);
        assertEquals(0, page.first().getPageNumber());
        assertEquals(10, page.first().getPageSize());
    }

    @Test
    void distinctByKeyTest() {
        List<UserProfile> userProfiles = new ArrayList<UserProfile>();
        userProfiles.add(TestSupport.createUserProfile());
        userProfiles.add(TestSupport.createUserProfile());
        List<UserProfile> distinctByPersonalCode = userProfiles.stream()
                .filter(distinctByKeys(p -> p.getPersonalCode()))
                .collect(Collectors.toList());
        assertThat(distinctByPersonalCode).hasSize(1);

    }

    @Test
    void distinctByKeyTestWhenKeysNotProvided() {
        List<UserProfile> userProfiles = new ArrayList<UserProfile>();

        List<UserProfile> distinctByPersonalCode = userProfiles.stream()
                .filter(distinctByKeys(p -> p.getPersonalCode()))
                .collect(Collectors.toList());
        assertThat(distinctByPersonalCode).isNullOrEmpty();

    }

    @Test
    void distinctByKeyTestWhenTwoKeysProvided() {
        UserProfile userProfile = TestSupport.createUserProfile();
        userProfile.setPersonalCode(null);
        List<UserProfile> userProfiles = new ArrayList<UserProfile>();
        userProfiles.add(userProfile);
        userProfiles.add(TestSupport.createUserProfile());
        List<UserProfile> distinctByPersonalCode = userProfiles.stream()
                .filter(distinctByKeys(p -> p.getPersonalCode()))
                .collect(Collectors.toList());
        assertThat(distinctByPersonalCode).hasSize(2);

    }

}
