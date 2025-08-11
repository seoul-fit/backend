package com.seoulfit.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리 설정
 * 
 * 스케줄링과 비동기 이벤트 처리를 위한 설정
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
    
    /**
     * 비동기 작업용 스레드 풀 설정
     * 
     * @return ThreadPoolTaskExecutor
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("UrbanPing-Async-");
        executor.setRejectedExecutionHandler((r, executor1) -> {
            log.warn("비동기 작업 거부됨: {}", r.toString());
        });
        executor.initialize();
        return executor;
    }
}
