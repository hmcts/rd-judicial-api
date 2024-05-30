package uk.gov.hmcts.reform.judicialapi.controller.util;

import io.jsonwebtoken.Jwts;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import uk.gov.hmcts.reform.judicialapi.controller.WelcomeController;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ForbiddenException;
import uk.gov.hmcts.reform.judicialapi.service.impl.FeatureToggleServiceImpl;
import uk.gov.hmcts.reform.judicialapi.util.FeatureConditionEvaluation;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeatureConditionEvaluationTest {

    final FeatureToggleServiceImpl featureToggleService = mock(FeatureToggleServiceImpl.class);
    @Spy
    FeatureConditionEvaluation featureConditionEvaluation = new FeatureConditionEvaluation(featureToggleService);
    final HttpServletRequest httpRequest = mock(HttpServletRequest.class);
    final HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    final HandlerMethod handlerMethod = mock(HandlerMethod.class);
    final Method method = mock(Method.class);

    @BeforeEach
    void before() {
        when(method.getName()).thenReturn("test");
        doReturn(WelcomeController.class).when(method).getDeclaringClass();
        when(handlerMethod.getMethod()).thenReturn(method);
    }

    @Test
    void testPreHandleValidFlag() throws Exception {
        Map<String, String> launchDarklyMap = Map.of("WelcomeController.test", "test-flag");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpRequest));
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        final String token = generateDummyS2SToken("rd_judicial_api");
        when(httpRequest.getHeader(FeatureConditionEvaluation.SERVICE_AUTHORIZATION))
                .thenReturn(FeatureConditionEvaluation.BEARER + token);
        when(featureToggleService.isFlagEnabled(anyString())).thenReturn(true);
        assertTrue(featureConditionEvaluation.preHandle(httpRequest, httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
                .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    void testPreHandleInvalidFlag() throws Exception {
        Map<String, String> launchDarklyMap = Map.of("WelcomeController.test", "test-flag");
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpRequest));
        final String token = generateDummyS2SToken("rd_judicial_api");
        when(httpRequest.getHeader(FeatureConditionEvaluation.SERVICE_AUTHORIZATION))
                .thenReturn(FeatureConditionEvaluation.BEARER + token);
        when(featureToggleService.isFlagEnabled(anyString())).thenReturn(false);
        assertThrows(ForbiddenException.class,() -> featureConditionEvaluation.preHandle(httpRequest,
                httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
                .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    void testPreHandleWithTokenWithoutBearer() throws Exception {
        Map<String, String> launchDarklyMap = Map.of("WelcomeController.test", "test-flag");
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpRequest));
        final String token = generateDummyS2SToken("rd_judicial_api");
        when(httpRequest.getHeader(FeatureConditionEvaluation.SERVICE_AUTHORIZATION)).thenReturn(token);
        when(featureToggleService.isFlagEnabled(anyString())).thenReturn(true);
        assertTrue(featureConditionEvaluation.preHandle(httpRequest, httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
                .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    void testPreHandleWithoutS2SHeader() throws Exception {
        Map<String, String> launchDarklyMap = Map.of("WelcomeController.test", "test-flag");
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpRequest));
        when(httpRequest.getHeader(FeatureConditionEvaluation.SERVICE_AUTHORIZATION)).thenReturn(StringUtils.EMPTY);
        when(featureToggleService.isFlagEnabled(anyString())).thenReturn(true);
        assertTrue(featureConditionEvaluation.preHandle(httpRequest, httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
                .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    void testPreHandleInvalidServletRequestAttributes() throws Exception {
        Map<String, String> launchDarklyMap = Map.of("WelcomeController.test", "test-flag");
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        assertThrows(ForbiddenException.class,() -> featureConditionEvaluation.preHandle(httpRequest,
                httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
                .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    void testPreHandleNoFlag() throws Exception {
        assertTrue(featureConditionEvaluation.preHandle(httpRequest, httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
                .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    void testPreHandleNonConfiguredValues() throws Exception {
        Map<String, String> launchDarklyMap = Map.of("DummyController.test", "test-flag");
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        assertTrue(featureConditionEvaluation.preHandle(httpRequest, httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
                .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    void testPreHandleWhenLdIsEmpty() throws Exception {
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(new HashMap<>());
        assertTrue(featureConditionEvaluation.preHandle(httpRequest, httpServletResponse, handlerMethod));
    }

    public static String generateDummyS2SToken(String serviceName) {
        return Jwts.builder()
                .subject(serviceName)
                .issuedAt(new Date())
                .signWith(Jwts.SIG.HS256.key().build())
                .compact();
    }
}
