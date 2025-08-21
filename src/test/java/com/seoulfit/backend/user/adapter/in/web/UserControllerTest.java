package com.seoulfit.backend.user.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoulfit.backend.user.adapter.in.web.dto.UpdateUserRequest;
import com.seoulfit.backend.user.application.port.in.ManageUserUseCase;
import com.seoulfit.backend.user.application.port.in.dto.UpdateUserCommand;
import com.seoulfit.backend.user.application.port.in.dto.UserResult;
import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
@ActiveProfiles("test")
@DisplayName("UserController 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ManageUserUseCase manageUserUseCase;

    @Test
    @DisplayName("사용자 조회 - 성공")
    void getUser_Success() throws Exception {
        // given
        Long userId = 1L;
        UserResult userResult = createTestUserResult(userId);
        given(manageUserUseCase.getUser(userId)).willReturn(userResult);

        // when & then
        mockMvc.perform(get("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.nickname").value("테스트유저"))
                .andExpect(jsonPath("$.profileImageUrl").value("https://example.com/profile.jpg"))
                .andExpect(jsonPath("$.locationAddress").value("서울시 강남구"));
    }

    @Test
    @DisplayName("내 정보 조회 - 성공")
    void getMyInfo_Success() throws Exception {
        // given
        String oauthUserId = "kakao123456";
        String oauthProvider = "kakao";
        UserResult userResult = createTestUserResult(1L);
        given(manageUserUseCase.getUserByOAuth(oauthUserId, AuthProvider.KAKAO))
                .willReturn(userResult);

        // when & then
        mockMvc.perform(get("/api/users/me")
                .param("oauthUserId", oauthUserId)
                .param("oauthProvider", oauthProvider)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nickname").value("테스트유저"));
    }

    @Test
    @DisplayName("사용자 정보 수정 - 성공")
    void updateUser_Success() throws Exception {
        // given
        Long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest();
        // Reflection을 사용하여 private 필드 설정
        java.lang.reflect.Field nicknameField = UpdateUserRequest.class.getDeclaredField("nickname");
        nicknameField.setAccessible(true);
        nicknameField.set(request, "수정된닉네임");
        
        java.lang.reflect.Field profileImageUrlField = UpdateUserRequest.class.getDeclaredField("profileImageUrl");
        profileImageUrlField.setAccessible(true);
        profileImageUrlField.set(request, "https://example.com/new-profile.jpg");
        
        java.lang.reflect.Field locationLatitudeField = UpdateUserRequest.class.getDeclaredField("locationLatitude");
        locationLatitudeField.setAccessible(true);
        locationLatitudeField.set(request, 37.5665);
        
        java.lang.reflect.Field locationLongitudeField = UpdateUserRequest.class.getDeclaredField("locationLongitude");
        locationLongitudeField.setAccessible(true);
        locationLongitudeField.set(request, 126.9780);
        
        java.lang.reflect.Field locationAddressField = UpdateUserRequest.class.getDeclaredField("locationAddress");
        locationAddressField.setAccessible(true);
        locationAddressField.set(request, "서울시 중구");
        
        java.lang.reflect.Field interestsField = UpdateUserRequest.class.getDeclaredField("interests");
        interestsField.setAccessible(true);
        interestsField.set(request, List.of(InterestCategory.SPORTS, InterestCategory.CULTURE));

        UserResult updatedResult = createTestUserResult(userId);
        given(manageUserUseCase.updateUser(any(UpdateUserCommand.class)))
                .willReturn(updatedResult);

        // when & then
        mockMvc.perform(put("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.nickname").exists());

        verify(manageUserUseCase).updateUser(any(UpdateUserCommand.class));
    }

    @Test
    @DisplayName("사용자 삭제 - 성공")
    void deleteUser_Success() throws Exception {
        // given
        Long userId = 1L;
        doNothing().when(manageUserUseCase).deleteUser(userId);

        // when & then
        mockMvc.perform(delete("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(manageUserUseCase).deleteUser(userId);
    }

    @Test
    @DisplayName("사용자 조회 - 사용자 없음")
    void getUser_NotFound() throws Exception {
        // given
        Long userId = 999L;
        given(manageUserUseCase.getUser(userId))
                .willThrow(new RuntimeException("User not found"));

        // when & then
        mockMvc.perform(get("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("내 정보 조회 - 필수 파라미터 누락")
    void getMyInfo_MissingParameters() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private UserResult createTestUserResult(Long userId) {
        return UserResult.builder()
                .id(userId)
                .authUserId(100L)
                .nickname("테스트유저")
                .profileImageUrl("https://example.com/profile.jpg")
                .locationLatitude(37.5665)
                .locationLongitude(126.9780)
                .locationAddress("서울시 강남구")
                .status(UserStatus.ACTIVE)
                .interests(List.of(InterestCategory.SPORTS, InterestCategory.CULTURE))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}