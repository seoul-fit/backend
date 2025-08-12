package com.seoulfit.backend.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * REST 클라이언트 설정
 * <p>
 * 외부 API 호출을 위한 RestTemplate 및 RestClient 설정
 * OAuth 토큰 교환을 위한 설정 추가
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class RestClientConfig {

    /**
     * 기본 RestTemplate 빈
     * OAuth 토큰 교환 등에 사용
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(customRequestFactory());
        
        // 요청/응답 로깅 인터셉터 추가
        restTemplate.setInterceptors(List.of(loggingInterceptor()));
        
        return restTemplate;
    }

    /**
     * OAuth 전용 RestTemplate 빈
     * OAuth 토큰 교환에 특화된 설정
     */
    @Bean("oauthRestTemplate")
    public RestTemplate oauthRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(oauthRequestFactory());
        
        // OAuth 요청 로깅 인터셉터
        restTemplate.setInterceptors(List.of(oauthLoggingInterceptor()));
        
        return restTemplate;
    }

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
                .messageConverters(converters -> {
                    // XML 처리를 위한 컨버터 추가 (JavaTimeModule 포함)
                    XmlMapper xmlMapper = new XmlMapper();
                    xmlMapper.registerModule(new JavaTimeModule());
                    xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                    MappingJackson2XmlHttpMessageConverter xmlConverter = new MappingJackson2XmlHttpMessageConverter(xmlMapper);
                    converters.add(0, xmlConverter); // 첫 번째로 추가하여 우선적으로 처리
                    
                    // JSON 처리를 위한 컨버터도 유지 (JavaTimeModule 포함)
                    ObjectMapper jsonMapper = new ObjectMapper();
                    jsonMapper.registerModule(new JavaTimeModule());
                    jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                    MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(jsonMapper);
                    converters.add(1, jsonConverter);
                })
                .requestInterceptor((request, body, execution) -> {
                    log.debug("Seoul API Request: {} {}", request.getMethod(), request.getURI());
                    try {
                        var response = execution.execute(request, body);
                        log.debug("Seoul API Response Status: {} Content-Type: {}", 
                                response.getStatusCode(), 
                                response.getHeaders().getContentType());
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

    private ClientHttpRequestFactory oauthRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(15000);
        requestFactory.setReadTimeout(15000);
        return requestFactory;
    }

    private ClientHttpRequestFactory seoulApiRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(60000);
        requestFactory.setReadTimeout(60000);
        return requestFactory;
    }

    /**
     * 일반 요청 로깅 인터셉터
     */
    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.debug("REST 요청: {} {}", request.getMethod(), request.getURI());
            
            var response = execution.execute(request, body);
            
            log.debug("REST 응답: {} - {}", response.getStatusCode(), request.getURI());
            return response;
        };
    }

    /**
     * OAuth 요청 로깅 인터셉터
     */
    private ClientHttpRequestInterceptor oauthLoggingInterceptor() {
        return (request, body, execution) -> {
            log.info("OAuth 요청: {} {}", request.getMethod(), request.getURI());
            
            var response = execution.execute(request, body);
            
            log.info("OAuth 응답: {} - {}", response.getStatusCode(), request.getURI());
            return response;
        };
    }
}
