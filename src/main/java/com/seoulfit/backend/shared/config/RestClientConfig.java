package com.seoulfit.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .requestFactory(customRequestFactory())
                .requestInterceptor((request, body, execution) -> {
                    log.debug("HTTP Request: {} {}", request.getMethod(), request.getURI());
                    return execution.execute(request, body);
                })
                .build();
    }

    @Bean("seoulApiRestClient")
    public RestClient seoulApiRestClient() {
        return RestClient.builder()
                .requestFactory(seoulApiRequestFactory())
                .requestInterceptor((request, body, execution) -> {
                    log.debug("Seoul API Request: {} {}", request.getMethod(), request.getURI());
                    try {
                        var response = execution.execute(request, body);
                        log.debug("Seoul API Response Status: {}", response.getStatusCode());
                        return response;
                    } catch (Exception e) {
                        log.error("Seoul API Request failed: {} {}", request.getMethod(), request.getURI(), e);
                        throw e;
                    }
                })
                .build();
    }

    private ClientHttpRequestFactory customRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(30000);
        requestFactory.setReadTimeout(30000);
        return requestFactory;
    }

    private ClientHttpRequestFactory seoulApiRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(60000);
        requestFactory.setReadTimeout(60000);
        return requestFactory;
    }
}
