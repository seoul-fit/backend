package com.seoulfit.backend.presentation;

import com.seoulfit.backend.application.security.CustomUserDetails;
import com.seoulfit.backend.application.service.AuthService;
import com.seoulfit.backend.domain.OAuthProvider;
import com.seoulfit.backend.domain.User;
import com.seoulfit.backend.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "이메일과 비밀번호로 회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        TokenResponse response = authService.signUp(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "OAuth 회원가입", description = "OAuth 정보로 회원가입을 진행합니다.")
    @PostMapping("/oauth/signup")
    public ResponseEntity<TokenResponse> oauthSignUp(@Valid @RequestBody OAuthSignUpRequest request) {
        TokenResponse response = authService.oauthSignUp(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인을 진행합니다.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "OAuth 로그인", description = "OAuth 정보로 로그인을 진행합니다.")
    @PostMapping("/oauth/login")
    public ResponseEntity<TokenResponse> oauthLogin(@Valid @RequestBody OAuthLoginRequest request) {
        TokenResponse response = authService.oauthLogin(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "OAuth 사용자 확인", description = "OAuth 사용자 존재 여부를 확인합니다.")
    @GetMapping("/oauth/check")
    public ResponseEntity<OAuthUserCheckResponse> checkOAuthUser(
            @RequestParam OAuthProvider provider,
            @RequestParam String oauthUserId) {
        
        boolean exists = authService.isOAuthUserExists(provider, oauthUserId);
        User user = null;
        
        if (exists) {
            user = authService.getOAuthUser(provider, oauthUserId);
        }
        
        OAuthUserCheckResponse response = OAuthUserCheckResponse.builder()
                .exists(exists)
                .user(user != null ? UserResponse.from(user) : null)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 액세스 토큰을 갱신합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        TokenResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = UserResponse.from(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "OAuth 로그인 URL", description = "OAuth 로그인을 위한 URL을 제공합니다.")
    @GetMapping("/oauth/{provider}")
    public ResponseEntity<String> getOAuthLoginUrl(@PathVariable String provider) {
        String loginUrl = "/oauth2/authorization/" + provider;
        return ResponseEntity.ok(loginUrl);
    }
}
