package com.seoulfit.backend.user.adapter.in.web;

import com.seoulfit.backend.user.adapter.in.web.dto.*;
import com.seoulfit.backend.user.application.port.in.AuthenticateUserUseCase;
import com.seoulfit.backend.user.application.port.in.dto.*;
import com.seoulfit.backend.user.application.service.OAuthService;
import com.seoulfit.backend.user.domain.AuthProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 인증 컨트롤러
 * <p>
 * 헥사고날 아키텍처의 입력 어댑터
 * 사용자 인증과 관련된 HTTP 요청을 처리
 * 카카오 공식 문서 기준으로 최신 OAuth 2.0 Authorization Code Flow 지원
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "사용자 인증 API - 카카오 공식 문서 기준")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final OAuthService oAuthService;

    @Operation(summary = "OAuth 사용자 확인", description = "OAuth 사용자 존재 여부를 확인합니다.")
    @PostMapping("/oauth/check")
    public ResponseEntity<OAuthUserCheckResponse> checkOAuthUser(@Valid @RequestBody OAuthLoginRequest request) {
        // 기존 방식과의 호환성을 위해 유지
        if (request.getOauthUserId() != null) {
            OAuthUserCheckCommand command = OAuthUserCheckCommand.of(
                    request.getProvider(),
                    request.getOauthUserId()
            );
            OAuthUserCheckResult result = authenticateUserUseCase.checkOAuthUser(command);
            return ResponseEntity.ok(OAuthUserCheckResponse.from(result));
        }

        // Authorization Code Flow는 별도 엔드포인트에서 처리
        throw new IllegalArgumentException("OAuth 사용자 확인을 위해서는 oauthUserId가 필요합니다.");
    }

    @Operation(
        summary = "OAuth 로그인 (Authorization Code Flow)", 
        description = "OAuth 권한부여 승인코드를 사용하여 로그인합니다. 카카오 공식 문서 기준으로 구현되었습니다."
    )
    @PostMapping("/oauth/login")
    public ResponseEntity<TokenResponse> oauthLogin(@Valid @RequestBody OAuthLoginRequest request) {
        log.info("OAuth 로그인 요청: provider={}", request.getProvider());

        // Authorization Code Flow 방식 (권장)
        if (request.getAuthorizationCode() != null && request.getRedirectUri() != null) {
            OAuthAuthorizationCommand command = OAuthAuthorizationCommand.of(
                    request.getProvider(),
                    request.getAuthorizationCode(),
                    request.getRedirectUri()
            );
            TokenResult result = authenticateUserUseCase.oauthLoginWithAuthorizationCode(command);
            return ResponseEntity.ok(TokenResponse.from(result));
        }

        // 기존 방식 (deprecated)
        if (request.getOauthUserId() != null) {
            log.warn("Deprecated OAuth 로그인 방식 사용: provider={}", request.getProvider());
            OAuthLoginCommand command = OAuthLoginCommand.ofLegacy(
                    request.getProvider(),
                    request.getOauthUserId()
            );
            TokenResult result = authenticateUserUseCase.oauthLogin(command);
            return ResponseEntity.ok(TokenResponse.from(result));
        }

        throw new IllegalArgumentException("authorizationCode와 redirectUri 또는 oauthUserId가 필요합니다.");
    }

    @Operation(
        summary = "OAuth 로그인 (간편 방식)", 
        description = "OAuth 권한부여 승인코드만으로 간편하게 로그인합니다."
    )
    @PostMapping("/oauth/login/simple")
    public ResponseEntity<TokenResponse> oauthLoginSimple(
            @Parameter(description = "OAuth 제공자 (KAKAO, GOOGLE 등)") @RequestParam("provider") String provider,
            @Parameter(description = "권한부여 승인코드") @RequestParam("code") String authorizationCode,
            @Parameter(description = "리다이렉트 URI") @RequestParam("redirectUri") String redirectUri) {
        
        log.info("OAuth 간편 로그인 요청: provider={}", provider);

        OAuthAuthorizationCommand command = OAuthAuthorizationCommand.of(
                AuthProvider.valueOf(provider.toUpperCase()),
                authorizationCode,
                redirectUri
        );
        
        TokenResult result = authenticateUserUseCase.oauthLoginWithAuthorizationCode(command);
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

    @Operation(
        summary = "OAuth 인증 URL 생성", 
        description = "OAuth 제공자의 인증 URL을 생성합니다. 카카오 공식 문서 기준으로 구현되었습니다."
    )
    @GetMapping("/oauth/url/{provider}")
    public ResponseEntity<Map<String, String>> getOAuthUrl(
            @Parameter(description = "OAuth 제공자") @PathVariable String provider,
            @Parameter(description = "리다이렉트 URI") @RequestParam String redirectUri,
            @Parameter(description = "추가 동의항목 (선택사항)") @RequestParam(required = false) String scope,
            @Parameter(description = "상태값 (CSRF 방지용)") @RequestParam(required = false) String state) {
        
        try {
            AuthProvider authProvider = AuthProvider.valueOf(provider.toUpperCase());
            String authUrl = oAuthService.generateAuthUrl(authProvider, redirectUri, scope, state);
            
            return ResponseEntity.ok(Map.of(
                "authUrl", authUrl,
                "provider", provider,
                "redirectUri", redirectUri
            ));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + provider);
        }
    }

    @Operation(
        summary = "OAuth 로그아웃", 
        description = "OAuth 제공자에서 로그아웃 처리합니다. 카카오의 경우 세션이 종료됩니다."
    )
    @PostMapping("/oauth/logout")
    public ResponseEntity<Map<String, Object>> oauthLogout(
            @Parameter(description = "OAuth 제공자") @RequestParam String provider,
            @Parameter(description = "액세스 토큰") @RequestParam String accessToken) {
        
        try {
            AuthProvider authProvider = AuthProvider.valueOf(provider.toUpperCase());
            Map<String, Object> result = oAuthService.logout(authProvider, accessToken);
            
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + provider);
        }
    }

    @Operation(
        summary = "OAuth 연결 해제", 
        description = "OAuth 제공자와의 연결을 해제합니다. 카카오의 경우 앱과 사용자의 연결이 완전히 해제됩니다."
    )
    @PostMapping("/oauth/unlink")
    public ResponseEntity<Map<String, Object>> oauthUnlink(
            @Parameter(description = "OAuth 제공자") @RequestParam String provider,
            @Parameter(description = "액세스 토큰") @RequestParam String accessToken) {
        
        try {
            AuthProvider authProvider = AuthProvider.valueOf(provider.toUpperCase());
            Map<String, Object> result = oAuthService.unlink(authProvider, accessToken);
            
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + provider);
        }
    }

    @Operation(
        summary = "OAuth 인가 코드 요청 (리다이렉트)", 
        description = "카카오 인증 서버로 리다이렉트하여 인가 코드를 요청합니다."
    )
    @GetMapping("/oauth/authorize/{provider}")
    public ResponseEntity<Void> authorize(
            @Parameter(description = "OAuth 제공자") @PathVariable String provider,
            @Parameter(description = "리다이렉트 URI") @RequestParam String redirectUri,
            @Parameter(description = "추가 동의항목 (선택사항)") @RequestParam(required = false) String scope,
            @Parameter(description = "상태값 (CSRF 방지용)") @RequestParam(required = false) String state) {
        
        try {
            AuthProvider authProvider = AuthProvider.valueOf(provider.toUpperCase());
            String authUrl = oAuthService.generateAuthUrl(authProvider, redirectUri, scope, state);
            
            return ResponseEntity.status(302)
                    .header("Location", authUrl)
                    .build();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + provider);
        }
    }
}
