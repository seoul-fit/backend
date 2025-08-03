package com.seoulfit.backend.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient 설정
 * 
 * 공공 API 호출을 위한 WebClient 빈 설정
 * 타임아웃, 로깅, 에러 처리 등을 포함
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class WebClientConfig {
    
    @Value("${urbanping.api.seoul.timeout:10}")
    private int timeoutSeconds;
    
    /**
     * 공공 API 호출용 WebClient 빈 생성
     * 
     * @return 설정된 WebClient 인스턴스
     */
    @Bean
    public WebClient webClient() {
        // HTTP 클라이언트 설정
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutSeconds * 1000)
                .responseTimeout(Duration.ofSeconds(timeoutSeconds))
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(timeoutSeconds, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(timeoutSeconds, TimeUnit.SECONDS)));
        
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logRequest())
                .filter(logResponse())
                .filter(handleError())
                .build();
    }
    
    /**
     * 요청 로깅 필터
     * 
     * @return 요청 로깅 ExchangeFilterFunction
     */
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.debug("API 요청: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }
    
    /**
     * 응답 로깅 필터
     * 
     * @return 응답 로깅 ExchangeFilterFunction
     */
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.debug("API 응답: {} {}", clientResponse.statusCode(), clientResponse.headers().asHttpHeaders());
            return Mono.just(clientResponse);
        });
    }
    
    /**
     * 에러 처리 필터
     * 
     * @return 에러 처리 ExchangeFilterFunction
     */
    private ExchangeFilterFunction handleError() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                log.error("API 호출 에러: {} {}", clientResponse.statusCode(), clientResponse.headers().asHttpHeaders());
            }
            return Mono.just(clientResponse);
        });
    }
}
