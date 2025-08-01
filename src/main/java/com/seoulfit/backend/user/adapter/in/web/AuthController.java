package com.seoulfit.backend.user.adapter.in.web;

import com.seoulfit.backend.user.adapter.in.web.dto.*;
import com.seoulfit.backend.user.application.port.in.AuthenticateUserUseCase;
import com.seoulfit.backend.user.application.port.in.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 컨트롤러
 * <p>
 * 헥사고날 아키텍처의 입력 어댑터
 * 사용자 인증과 관련된 HTTP 요청을 처리
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "사용자 인증 API")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;

    @Operation(summary = "일반 로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = LoginCommand.of(request.getEmail(), request.getPassword());
        TokenResult result = authenticateUserUseCase.login(command);
        return ResponseEntity.ok(TokenResponse.from(result));
    }

    @Operation(summary = "일반 회원가입", description = "이메일과 비밀번호로 회원가입합니다.")
    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        SignUpCommand command = SignUpCommand.of(
                request.getEmail(),
                request.getPassword(),
                request.getNickname()
        );
        TokenResult result = authenticateUserUseCase.signUp(command);
        return ResponseEntity.ok(TokenResponse.from(result));
    }

    @Operation(summary = "OAuth 사용자 확인", description = "OAuth 사용자 존재 여부를 확인합니다.")
    @PostMapping("/oauth/check")
    public ResponseEntity<OAuthUserCheckResponse> checkOAuthUser(@Valid @RequestBody OAuthLoginRequest request) {
        OAuthUserCheckCommand command = OAuthUserCheckCommand.of(
                request.getProvider(),
                request.getOauthUserId()
        );
        OAuthUserCheckResult result = authenticateUserUseCase.checkOAuthUser(command);
        return ResponseEntity.ok(OAuthUserCheckResponse.from(result));
    }

    @Operation(summary = "OAuth 로그인", description = "OAuth 정보로 로그인합니다.")
    @PostMapping("/oauth/login")
    public ResponseEntity<TokenResponse> oauthLogin(@Valid @RequestBody OAuthLoginRequest request) {
        OAuthLoginCommand command = OAuthLoginCommand.of(
                request.getProvider(),
                request.getOauthUserId()
        );
        TokenResult result = authenticateUserUseCase.oauthLogin(command);
        return ResponseEntity.ok(TokenResponse.from(result));
    }

    @Operation(summary = "OAuth 회원가입", description = "OAuth 정보로 회원가입합니다.")
    @PostMapping("/oauth/signup")
    public ResponseEntity<TokenResponse> oauthSignUp(@Valid @RequestBody OAuthSignUpRequest request) {
        OAuthSignUpCommand command = OAuthSignUpCommand.of(
                request.getProvider(),
                request.getOauthUserId(),
                request.getNickname(),
                request.getEmail(),
                request.getProfileImageUrl(),
                request.getInterests()
        );
        TokenResult result = authenticateUserUseCase.oauthSignUp(command);
        return ResponseEntity.ok(TokenResponse.from(result));
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestParam String refreshToken) {
        TokenResult result = authenticateUserUseCase.refreshToken(refreshToken);
        return ResponseEntity.ok(TokenResponse.from(result));
    }

    @Operation(summary = "이메일 중복 확인", description = "이메일 중복 여부를 확인합니다.")
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = authenticateUserUseCase.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}
