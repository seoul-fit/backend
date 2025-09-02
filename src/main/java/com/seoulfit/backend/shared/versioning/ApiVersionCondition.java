package com.seoulfit.backend.shared.versioning;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * API 버전 매칭 조건
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {

    private static final Pattern VERSION_PATTERN = Pattern.compile("/api/(v\\d+)/.*");
    
    private final Set<String> versions;
    private final ApiVersion apiVersion;

    public ApiVersionCondition(ApiVersion apiVersion) {
        this.apiVersion = apiVersion;
        this.versions = new HashSet<>(Arrays.asList(apiVersion.value()));
    }

    private ApiVersionCondition(Set<String> versions, ApiVersion apiVersion) {
        this.versions = versions;
        this.apiVersion = apiVersion;
    }

    @Override
    public ApiVersionCondition combine(ApiVersionCondition other) {
        if (other == null) {
            return this;
        }
        
        Set<String> combinedVersions = new HashSet<>(this.versions);
        combinedVersions.addAll(other.versions);
        
        return new ApiVersionCondition(combinedVersions, 
            this.apiVersion != null ? this.apiVersion : other.apiVersion);
    }

    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        String path = request.getRequestURI();
        Matcher matcher = VERSION_PATTERN.matcher(path);
        
        if (matcher.matches()) {
            String version = matcher.group(1);
            
            if (versions.contains(version)) {
                // Deprecated 버전 경고 헤더 추가
                if (apiVersion != null && apiVersion.deprecated()) {
                    request.setAttribute("X-API-Deprecated", "true");
                    request.setAttribute("X-API-Deprecated-Date", apiVersion.deprecationDate());
                    request.setAttribute("X-API-Alternative-Version", apiVersion.alternativeVersion());
                }
                
                return this;
            }
        }
        
        return null;
    }

    @Override
    public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
        if (other == null) {
            return -1;
        }
        
        // 더 최신 버전을 우선시
        String maxThis = Collections.max(this.versions);
        String maxOther = Collections.max(other.versions);
        
        return maxOther.compareTo(maxThis);
    }

    public Set<String> getVersions() {
        return versions;
    }
}