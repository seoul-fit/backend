package com.seoulfit.backend.trigger.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoulfit.backend.config.TestSecurityConfig;
import com.seoulfit.backend.trigger.adapter.in.web.dto.LocationTriggerRequest;
import com.seoulfit.backend.trigger.application.port.in.EvaluateTriggerUseCase;
import com.seoulfit.backend.trigger.application.port.in.dto.LocationTriggerCommand;
import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TriggerController 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@WebMvcTest(TriggerController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("TriggerController 단위 테스트")
class TriggerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private EvaluateTriggerUseCase evaluateTriggerUseCase;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Nested
    @DisplayName("위치 기반 트리거 평가 API 테스트")
    class EvaluateLocationTriggerTest {
        
        @Test
        @DisplayName("위치 기반 트리거 평가 - 성공")
        void evaluateLocationTrigger_Success() throws Exception {
            // given
            LocationTriggerRequest request = new LocationTriggerRequest(37.5665, 126.9780, 2000, null, false);
            // userId 설정 (reflection 사용)
            setUserId(request, "user123");
            
            TriggerEvaluationResult result = TriggerEvaluationResult.builder()
                .triggered(true)
                .triggeredCount(2)
                .totalEvaluated(5)
                .triggeredList(Arrays.asList(
                    TriggerEvaluationResult.TriggeredInfo.builder()
                        .triggerType("WEATHER")
                        .title("미세먼지 경보")
                        .message("현재 위치의 미세먼지가 나쁨 수준입니다.")
                        .priority(1)
                        .triggeredTime(LocalDateTime.now())
                        .build(),
                    TriggerEvaluationResult.TriggeredInfo.builder()
                        .triggerType("CULTURAL_EVENT")
                        .title("근처 문화 행사")
                        .message("500m 이내에서 축제가 진행 중입니다.")
                        .priority(2)
                        .triggeredTime(LocalDateTime.now())
                        .build()
                ))
                .evaluationTime(LocalDateTime.now())
                .build();
            
            when(evaluateTriggerUseCase.evaluateLocationBasedTriggers(any(LocationTriggerCommand.class)))
                .thenReturn(result);
            
            // when & then
            mockMvc.perform(post("/api/triggers/evaluate/location")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.triggered").value(true))
                .andExpect(jsonPath("$.triggeredCount").value(2))
                .andExpect(jsonPath("$.triggeredList", hasSize(2)));
            
            verify(evaluateTriggerUseCase, times(1)).evaluateLocationBasedTriggers(any(LocationTriggerCommand.class));
        }
        
        @Test
        @DisplayName("위치 기반 트리거 평가 - 트리거 없음")
        void evaluateLocationTrigger_NoTriggers() throws Exception {
            // given
            LocationTriggerRequest request = new LocationTriggerRequest(37.5665, 126.9780, 2000, null, false);
            // userId 설정 (reflection 사용)
            setUserId(request, "user123");
            
            TriggerEvaluationResult emptyResult = TriggerEvaluationResult.builder()
                .triggered(false)
                .triggeredCount(0)
                .totalEvaluated(5)
                .triggeredList(Collections.emptyList())
                .evaluationTime(LocalDateTime.now())
                .build();
            
            when(evaluateTriggerUseCase.evaluateLocationBasedTriggers(any(LocationTriggerCommand.class)))
                .thenReturn(emptyResult);
            
            // when & then
            mockMvc.perform(post("/api/triggers/evaluate/location")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.triggered").value(false))
                .andExpect(jsonPath("$.triggeredCount").value(0))
                .andExpect(jsonPath("$.triggeredList", hasSize(0)));
        }
        
        @Test
        @DisplayName("위치 기반 트리거 평가 - 유효하지 않은 좌표")
        void evaluateLocationTrigger_InvalidCoordinates() throws Exception {
            // given
            String invalidRequest = "{\"latitude\":91.0,\"longitude\":126.9780,\"radius\":2000}";
            
            // when & then
            mockMvc.perform(post("/api/triggers/evaluate/location")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
        }
        
        @Test
        @DisplayName("위치 기반 트리거 평가 - 필수 필드 누락")
        void evaluateLocationTrigger_MissingRequiredFields() throws Exception {
            // given
            String invalidRequest = "{\"radius\":2000}";
            
            // when & then
            mockMvc.perform(post("/api/triggers/evaluate/location")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
        }
    }
    
    // 헬퍼 메서드
    private void setUserId(LocationTriggerRequest request, String userId) {
        try {
            java.lang.reflect.Field userIdField = LocationTriggerRequest.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(request, userId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set userId", e);
        }
    }
}