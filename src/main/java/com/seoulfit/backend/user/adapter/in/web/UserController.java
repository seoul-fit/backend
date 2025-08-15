package com.seoulfit.backend.user.adapter.in.web;

import com.seoulfit.backend.user.application.port.in.ManageUserUseCase;
import com.seoulfit.backend.user.application.port.in.dto.UpdateUserCommand;
import com.seoulfit.backend.user.application.port.in.dto.UserResult;
import com.seoulfit.backend.user.adapter.in.web.dto.UpdateUserRequest;
import com.seoulfit.backend.user.domain.AuthProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 사용자 컨트롤러
 * <p>
 * 헥사고날 아키텍처의 입력 어댑터
 * 사용자 프로필과 관련된 HTTP 요청을 처리
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관리 API")
public class UserController {

    private final ManageUserUseCase manageUserUseCase;

    @Operation(summary = "사용자 조회", description = "사용자 ID로 사용자 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResult> getUser(@PathVariable Long userId) {
        UserResult result = manageUserUseCase.getUser(userId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "내 정보 조회", description = "OAuth 인증 정보로 사용자 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserResult> getMyInfo(
            @RequestParam String oauthUserId,
            @RequestParam String oauthProvider) {
        AuthProvider provider = AuthProvider.fromCode(oauthProvider);
        UserResult result = manageUserUseCase.getUserByOAuth(oauthUserId, provider);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다.")
    @PutMapping("/{userId}")
    public ResponseEntity<UserResult> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        
        UpdateUserCommand command = UpdateUserCommand.of(
                userId,
                request.getNickname(),
                request.getProfileImageUrl(),
                request.getLocationLatitude(),
                request.getLocationLongitude(),
                request.getLocationAddress(),
                request.getInterests()
        );
        
        UserResult result = manageUserUseCase.updateUser(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        manageUserUseCase.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
