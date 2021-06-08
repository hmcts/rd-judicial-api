package uk.gov.hmcts.reform.judicialapi.controller.service.impl;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.LDClient;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.judicialapi.service.impl.FeatureToggleServiceImpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FeatureToggleServiceImplTest {

    LDClient ldClient = mock(LDClient.class);
    FeatureToggleServiceImpl flaFeatureToggleService = mock(FeatureToggleServiceImpl.class);

    @Test
    public void testIsFlagDisabled() {
        flaFeatureToggleService = new FeatureToggleServiceImpl(ldClient, "rd");
        assertFalse(flaFeatureToggleService.isFlagEnabled("test", "test"));
    }

    @Test
    public void testIsFlagEnabled() {
        flaFeatureToggleService = new FeatureToggleServiceImpl(ldClient, "rd");
        ReflectionTestUtils.setField(flaFeatureToggleService, "environment", "executionEnvironment");
        LDUser user = new LDUser.Builder("rd")
                .firstName("rd")
                .custom("servicename", "rd")
                .custom("environment", "executionEnvironment")
                .build();
        when(ldClient.boolVariation("test1",user,false)).thenReturn(true);
        assertTrue(flaFeatureToggleService.isFlagEnabled("rd", "test1"));
    }

    @Test
    public void mapServiceToFlagTest() {
        flaFeatureToggleService = new FeatureToggleServiceImpl(ldClient, "rd");
        flaFeatureToggleService.mapServiceToFlag();
        assertTrue(flaFeatureToggleService.getLaunchDarklyMap().size() >= 1);
    }
}
