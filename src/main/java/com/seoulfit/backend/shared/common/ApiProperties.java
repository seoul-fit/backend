package com.seoulfit.backend.shared.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "seoulfit.api")
@Component
@Setter
@Getter
public class ApiProperties {
    
    private Seoul seoul = new Seoul();
    
    @Setter
    @Getter
    public static class Seoul {
        private String baseUrl = "http://openapi.seoul.go.kr:8088";
        private String apiKey;
        private int timeout = 10;
        private int retryAttempts = 3;
    }
}
