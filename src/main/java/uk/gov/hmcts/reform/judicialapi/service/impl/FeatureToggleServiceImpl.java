package uk.gov.hmcts.reform.judicialapi.service.impl;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.LDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.service.FeatureToggleService;

import java.util.Map;

@Service
public class FeatureToggleServiceImpl implements FeatureToggleService {

    // mapping ClassName.methodName -> flagName
    private static final Map<String, String> LAUNCH_DARKLY_MAP = Map.of(
        "TestingSupportController.createIdamUserProfiles", "rd-judicial-api-test-idam-users",
        "ElinksController.loadLocation", "jrd-elinks-location",
        "ElinksController.loadPeople", "jrd-elinks-load-people",
        "ElinksController.idamElasticSearch", "jrd-elinks-idam-elastic-search",
        "ElinksController.fetchIdamIds", "jrd-elinks-idam-sso-search",
        "ElinksController.loadLeavers", "jrd-elinks-leavers",
        "ElinksController.loadDeleted", "jrd-elinks-load-deleted",
        "ElinksController.publishSidamIdToAsb", "jrd-elinks-publish-service-bus",
        "JrdElinkController.retrieveUsers", "jrd-elinks-search-api",
        "JrdElinkController.refreshUserProfile", "jrd-elinks-refresh-api"
    );

    @Autowired
    private final LDClient ldClient;

    @Value("${launchdarkly.sdk.environment}")
    private String environment;

    private final String userName;

    @Autowired
    public FeatureToggleServiceImpl(LDClient ldClient, @Value("${launchdarkly.sdk.user}") String userName) {
        this.ldClient = ldClient;
        this.userName = userName;
    }

    @Override
    public boolean isFlagEnabled(String flagName) {
        LDUser user = new LDUser.Builder(userName)
            .firstName(userName)
            .custom("environment", environment)
            .build();

        return ldClient.boolVariation(flagName, user, false);
    }

    @Override
    public Map<String, String> getLaunchDarklyMap() {
        return LAUNCH_DARKLY_MAP;
    }
}




