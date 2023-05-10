package uk.gov.hmcts.reform.judicialapi.elinks.repository;



import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.judicialapi.elinks.response.UserSearchResponseWrapper;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileRepositoryTest {

    @Spy
    private ProfileRepository profileRepository;



    @Test
    void test_search_profile() {
        UserSearchResponseWrapper userSearchResponseOne = new UserSearchResponseWrapper();
        UserSearchResponseWrapper userSearchResponseTwo = new UserSearchResponseWrapper();
        userSearchResponseOne.setPersonalCode("123");
        userSearchResponseOne.setFullName("ABC");
        userSearchResponseTwo.setPersonalCode("345");
        userSearchResponseTwo.setFullName("DEF");

        when(profileRepository.findBySearchForString(any(),
            any(),any(),anyList(),anyList())).thenReturn(List.of(userSearchResponseOne,userSearchResponseTwo));

        List<UserSearchResponseWrapper> result = profileRepository
            .findBySearchForString("abc","def",
                "ghi",new ArrayList<>(),new ArrayList<>());

        assertThat(result.get(0).getPersonalCode()).isEqualTo(userSearchResponseOne.getPersonalCode());
        assertThat(result.get(0).getFullName()).isEqualTo(userSearchResponseOne.getFullName());
    }
}

