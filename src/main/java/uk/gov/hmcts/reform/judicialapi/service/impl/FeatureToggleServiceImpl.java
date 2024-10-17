package uk.gov.hmcts.reform.judicialapi.service.impl;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.LDClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.service.FeatureToggleService;

import java.util.HashMap;
import java.util.Map;

@Service
public class FeatureToggleServiceImpl implements FeatureToggleService {

    @Autowired
    private final LDClient ldClient;

    @Value("${launchdarkly.sdk.environment}")
    private String environment;

    private final String userName;

    private Map<String, String> launchDarklyMap;

    @Autowired
    public FeatureToggleServiceImpl(LDClient ldClient, @Value("${launchdarkly.sdk.user}") String userName) {
        this.ldClient = ldClient;
        this.userName = userName;
    }

    /** mapServiceToFlag.
     * add controller method name, flag name  in map to apply ld flag on api like below
     * launchDarklyMap.put("OrganisationExternalController.retrieveOrganisationsByStatusWithAddressDetailsOptional",
     * "prd-aac-get-org-by-status");
     */
    @PostConstruct
    public void mapServiceToFlag() {
        launchDarklyMap = new HashMap<>();
        launchDarklyMap.put("TestingSupportController.createIdamUserProfiles", "rd-judicial-api-test-idam-users");
        launchDarklyMap.put("ElinksController.loadLocation", "jrd-elinks-location");
        launchDarklyMap.put("ElinksController.loadPeople", "jrd-elinks-load-people");
        launchDarklyMap.put("ElinksController.idamElasticSearch", "jrd-elinks-idam-elastic-search");
        launchDarklyMap.put("ElinksController.fetchIdamIds", "jrd-elinks-idam-sso-search");
        launchDarklyMap.put("ElinksController.loadLeavers", "jrd-elinks-leavers");
        launchDarklyMap.put("ElinksController.loadDeleted", "jrd-elinks-load-deleted");
        launchDarklyMap.put("ElinksController.publishSidamIdToAsb", "jrd-elinks-publish-service-bus");
        launchDarklyMap.put("JrdElinkController.retrieveUsers", "jrd-elinks-search-api");
        launchDarklyMap.put("JrdElinkController.refreshUserProfile", "jrd-elinks-refresh-api");

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
        return launchDarklyMap;
    }
}




