package com.seoulfit.backend.user.adapter.in.web;

import com.seoulfit.backend.user.application.port.in.ManageUserInterestUseCase;
import com.seoulfit.backend.user.application.port.in.dto.UpdateUserInterestCommand;
import com.seoulfit.backend.user.application.port.in.dto.UserInterestResult;
import com.seoulfit.backend.user.infrastructure.security.CustomUserDetails;
import com.seoulfit.backend.user.adapter.in.web.dto.UpdateUserInterestRequest;
import com.seoulfit.backend.user.adapter.in.web.dto.UserInterestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관심사 관리 컨트롤러
 * Hexagonal Architecture의 인바운드 어댑터
 */
@Tag(name = "사용자 관심사", description = "사용자 관심사 관리 API")
@RestController
@RequestMapping("/api/users/interests")
@RequiredArgsConstructor
public class UserInterestController {

    private final ManageUserInterestUseCase manageUserInterestUseCase;

    @Operation(summary = "내 관심사 조회", description = "현재 로그인한 사용자의 관심사를 조회합니다.")
    @GetMapping
    public ResponseEntity<UserInterestResponse> getMyInterests(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        UserInterestResult result = manageUserInterestUseCase.getUserInterests(userDetails.getUser().getId());
        return ResponseEntity.ok(UserInterestResponse.from(result));
    }

    @Operation(summary = "내 관심사 업데이트", description = "현재 로그인한 사용자의 관심사를 업데이트합니다.")
    @PutMapping
    public ResponseEntity<UserInterestResponse> updateMyInterests(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateUserInterestRequest request) {
        
        UpdateUserInterestCommand command = UpdateUserInterestCommand.of(
                userDetails.getUser().getId(), 
                request.getInterests()
        );
        
        UserInterestResult result = manageUserInterestUseCase.updateUserInterests(command);
        return ResponseEntity.ok(UserInterestResponse.from(result));
    }

    @Operation(summary = "특정 사용자 관심사 조회", description = "특정 사용자의 관심사를 조회합니다. (관리자용)")
    @GetMapping("/{userId}")
    public ResponseEntity<UserInterestResponse> getUserInterests(@PathVariable Long userId) {
        
        UserInterestResult result = manageUserInterestUseCase.getUserInterests(userId);
        return ResponseEntity.ok(UserInterestResponse.from(result));
    }

    @Operation(summary = "특정 사용자 관심사 업데이트", description = "특정 사용자의 관심사를 업데이트합니다. (관리자용)")
    @PutMapping("/{userId}")
    public ResponseEntity<UserInterestResponse> updateUserInterests(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserInterestRequest request) {
        
        UpdateUserInterestCommand command = UpdateUserInterestCommand.of(userId, request.getInterests());
        UserInterestResult result = manageUserInterestUseCase.updateUserInterests(command);
        return ResponseEntity.ok(UserInterestResponse.from(result));
    }
}
