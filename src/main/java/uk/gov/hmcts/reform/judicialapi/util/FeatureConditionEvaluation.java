package uk.gov.hmcts.reform.judicialapi.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ForbiddenException;
import uk.gov.hmcts.reform.judicialapi.service.FeatureToggleService;

import java.util.Map;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Component
@AllArgsConstructor
public class FeatureConditionEvaluation implements HandlerInterceptor { 

    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    public static final String BEARER = "Bearer ";

    public static final String FORBIDDEN_EXCEPTION_LD = "feature flag is not released";

    @Autowired
    private final FeatureToggleService featureToggleService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {

        Map<String, String> launchDarklyUrlMap = featureToggleService.getLaunchDarklyMap();

        String restMethod = ((HandlerMethod) handler).getMethod().getName();
        String clazz = ((HandlerMethod) handler).getMethod().getDeclaringClass().getSimpleName();

        String flagName = launchDarklyUrlMap.get(clazz + "." + restMethod);

        if (isNotTrue(launchDarklyUrlMap.isEmpty()) && nonNull(flagName)) {

            boolean flagStatus = featureToggleService.isFlagEnabled(flagName);

            if (!flagStatus) {
                throw new ForbiddenException(flagName.concat(SPACE).concat(FORBIDDEN_EXCEPTION_LD));
            }
        }
        return true;
    }
}
