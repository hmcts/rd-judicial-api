package uk.gov.hmcts.reform.judicialapi.controller.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
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

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FeatureConditionEvaluationTest {

    FeatureToggleServiceImpl featureToggleService = mock(FeatureToggleServiceImpl.class);
    @Spy
    FeatureConditionEvaluation featureConditionEvaluation = new FeatureConditionEvaluation(featureToggleService);
    HttpServletRequest httpRequest = mock(HttpServletRequest.class);
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    HandlerMethod handlerMethod = mock(HandlerMethod.class);
    Method method = mock(Method.class);


    @Before
    public void before() {
        MockitoAnnotations.openMocks(this);
        when(method.getName()).thenReturn("test");
        doReturn(WelcomeController.class).when(method).getDeclaringClass();
        when(handlerMethod.getMethod()).thenReturn(method);
    }

    @Test
    public void testPreHandleValidFlag() throws Exception {
        Map<String, String> launchDarklyMap = new HashMap<>();
        launchDarklyMap.put("WelcomeController.test", "test-flag");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpRequest));
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        String token = generateDummyS2SToken("rd_judicial_api");
        when(httpRequest.getHeader(FeatureConditionEvaluation.SERVICE_AUTHORIZATION))
                .thenReturn(FeatureConditionEvaluation.BEARER + token);
        when(featureToggleService.isFlagEnabled(anyString(),anyString())).thenReturn(true);
        assertTrue(featureConditionEvaluation.preHandle(httpRequest, httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
                .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    public void testPreHandleInvalidFlag() throws Exception {
        Map<String, String> launchDarklyMap = new HashMap<>();
        launchDarklyMap.put("WelcomeController.test", "test-flag");
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpRequest));
        String token = generateDummyS2SToken("rd_judicial_api");
        when(httpRequest.getHeader(FeatureConditionEvaluation.SERVICE_AUTHORIZATION))
                .thenReturn(FeatureConditionEvaluation.BEARER + token);
        when(featureToggleService.isFlagEnabled(anyString(),anyString())).thenReturn(false);
        assertThrows(ForbiddenException.class,() -> featureConditionEvaluation.preHandle(httpRequest,
                httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
                .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    public void testPreHandleWithTokenWithoutBearer() throws Exception {
        Map<String, String> launchDarklyMap = new HashMap<>();
        launchDarklyMap.put("WelcomeController.test", "test-flag");
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpRequest));
        String token = generateDummyS2SToken("rd_judicial_api");
        when(httpRequest.getHeader(FeatureConditionEvaluation.SERVICE_AUTHORIZATION)).thenReturn(token);
        when(featureToggleService.isFlagEnabled(anyString(),anyString())).thenReturn(true);
        assertTrue(featureConditionEvaluation.preHandle(httpRequest, httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
                .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    public void testPreHandleWithoutS2SHeader() throws Exception {
        Map<String, String> launchDarklyMap = new HashMap<>();
        launchDarklyMap.put("WelcomeController.test", "test-flag");
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpRequest));
        when(httpRequest.getHeader(FeatureConditionEvaluation.SERVICE_AUTHORIZATION)).thenReturn(StringUtils.EMPTY);
        when(featureToggleService.isFlagEnabled(anyString(),anyString())).thenReturn(true);
        assertTrue(featureConditionEvaluation.preHandle(httpRequest, httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
                .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    public void testPreHandleInvalidServletRequestAttributes() throws Exception {
        Map<String, String> launchDarklyMap = new HashMap<>();
        launchDarklyMap.put("WelcomeController.test", "test-flag");
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        assertThrows(ForbiddenException.class,() -> featureConditionEvaluation.preHandle(httpRequest,
                httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
                .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    public void testPreHandleNoFlag() throws Exception {
        assertTrue(featureConditionEvaluation.preHandle(httpRequest, httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
                .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    public void testPreHandleNonConfiguredValues() throws Exception {
        Map<String, String> launchDarklyMap = new HashMap<>();
        launchDarklyMap.put("DummyController.test", "test-flag");
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        assertTrue(featureConditionEvaluation.preHandle(httpRequest, httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
                .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    public static String generateDummyS2SToken(String serviceName) {
        return Jwts.builder()
                .setSubject(serviceName)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.encode("AA"))
                .compact();
    }
}
