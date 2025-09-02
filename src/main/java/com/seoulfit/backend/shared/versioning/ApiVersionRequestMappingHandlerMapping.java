package com.seoulfit.backend.shared.versioning;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * API 버전 관리를 위한 RequestMappingHandlerMapping
 * 
 * @ApiVersion 어노테이션을 처리하여 버전별 URL 매핑을 생성합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private static final String VERSION_PREFIX = "/api/";

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        
        if (info == null) {
            return null;
        }

        ApiVersion methodVersion = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        ApiVersion typeVersion = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);

        ApiVersion apiVersion = methodVersion != null ? methodVersion : typeVersion;

        if (apiVersion != null) {
            return createVersionedMapping(info, apiVersion);
        }

        return info;
    }

    private RequestMappingInfo createVersionedMapping(RequestMappingInfo info, ApiVersion apiVersion) {
        String[] versions = apiVersion.value();
        
        // 각 버전에 대한 패턴 생성
        PatternsRequestCondition patternsCondition = new PatternsRequestCondition(
            Arrays.stream(versions)
                .flatMap(version -> 
                    info.getPatternsCondition().getPatterns().stream()
                        .map(pattern -> VERSION_PREFIX + version + pattern)
                )
                .toArray(String[]::new)
        );

        // 새로운 RequestMappingInfo 생성
        return new RequestMappingInfo(
            info.getName(),
            patternsCondition,
            info.getMethodsCondition(),
            info.getParamsCondition(),
            info.getHeadersCondition(),
            info.getConsumesCondition(),
            info.getProducesCondition(),
            info.getCustomCondition()
        );
    }

    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        ApiVersion apiVersion = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
        return apiVersion != null ? new ApiVersionCondition(apiVersion) : null;
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        ApiVersion apiVersion = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        return apiVersion != null ? new ApiVersionCondition(apiVersion) : null;
    }
}