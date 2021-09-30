package uk.gov.hmcts.reform.judicialapi.service;

import java.util.Map;

public interface FeatureToggleService {

    boolean isFlagEnabled(String flagName);

    Map<String, String> getLaunchDarklyMap();
}
