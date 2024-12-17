package uk.gov.hmcts.reform.judicialapi.controller.service.impl;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.LDClient;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.judicialapi.service.impl.FeatureToggleServiceImpl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FeatureToggleServiceImplTest {

    final LDClient ldClient = mock(LDClient.class);
    FeatureToggleServiceImpl flaFeatureToggleService = mock(FeatureToggleServiceImpl.class);

    @Test
    void testIsFlagDisabled() {
        flaFeatureToggleService = new FeatureToggleServiceImpl(ldClient, "rd");
        assertFalse(flaFeatureToggleService.isFlagEnabled("test"));
    }

    @Test
    void testIsFlagEnabled() {
        flaFeatureToggleService = new FeatureToggleServiceImpl(ldClient, "rd");
        ReflectionTestUtils.setField(flaFeatureToggleService, "environment", "executionEnvironment");
        final LDUser user = new LDUser.Builder("rd")
                .firstName("rd")
                .custom("environment", "executionEnvironment")
                .build();
        when(ldClient.boolVariation("test1",user,false)).thenReturn(true);
        assertTrue(flaFeatureToggleService.isFlagEnabled("test1"));
    }

}
