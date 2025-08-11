package com.seoulfit.backend.user.adapter.in.web;

import com.seoulfit.backend.trigger.application.port.in.EvaluateTriggerUseCase;
import com.seoulfit.backend.trigger.application.port.in.dto.LocationTriggerCommand;
import com.seoulfit.backend.trigger.application.port.in.dto.TriggerEvaluationResult;
import com.seoulfit.backend.trigger.adapter.in.web.dto.TriggerEvaluationResponse;
import com.seoulfit.backend.user.adapter.in.web.dto.*;
import com.seoulfit.backend.user.adapter.out.persistence.UserPort;
import com.seoulfit.backend.user.application.port.in.AuthenticateUserUseCase;
import com.seoulfit.backend.user.application.port.in.dto.*;
import com.seoulfit.backend.user.application.service.OAuthService;
import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.infrastructure.jwt.JwtTokenProvider;
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
 * @author Seoul Fit
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
    private final EvaluateTriggerUseCase evaluateTriggerUseCase;
    private final UserPort userPort;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
        summary = "01. OAuth 인가코드 검증",
        description = "프론트엔드에서 받은 인가코드로 OAuth 제공자에서 사용자 정보를 조회합니다. " +
                     "회원가입 전에 사용자 정보를 미리 확인할 때 사용합니다."
    )
    @PostMapping("/oauth/authorizecheck")
    public ResponseEntity<OAuthAuthorizeCheckResponse> checkAuthorizationCode(@Valid @RequestBody OAuthAuthorizeCheckRequest request) {
        log.info("OAuth 인가코드 검증 요청: provider={}", request.getProvider());

        OAuthAuthorizeCheckCommand command = OAuthAuthorizeCheckCommand.of(
                request.getProvider(),
                request.getAuthorizationCode(),
                request.getRedirectUri()
        );

        OAuthAuthorizeCheckResult result = authenticateUserUseCase.checkAuthorizationCode(command);
        return ResponseEntity.ok(OAuthAuthorizeCheckResponse.from(result));
    }

    @Operation(summary = "02. OAuth 사용자 확인", description = "OAuth 사용자 존재 여부를 확인합니다.")
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

    @Operation(summary = "03. OAuth 회원가입", description = "OAuth 정보로 회원가입합니다.")
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
        description = "OAuth 제공자에서 로그아웃 처리합니다. JWT 토큰으로 사용자를 조회하여 저장된 OAuth AccessToken을 사용합니다."
    )
    @PostMapping("/oauth/logout")
    public ResponseEntity<Map<String, Object>> oauthLogout(
            @Parameter(description = "사용자 JWT 토큰") @RequestHeader("Authorization") String authHeader) {
        
        try {
            // JWT 토큰에서 Bearer 제거
            String jwtToken = authHeader.replace("Bearer ", "");
            
            // JWT에서 사용자 ID 추출
            Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
            
            // 사용자 조회
            User user = userPort.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            // OAuth 사용자가 아닌 경우
            if (!user.isOAuthUser()) {
                return ResponseEntity.ok(Map.of("result", "OAuth 사용자가 아닙니다."));
            }
            
            // 유효한 OAuth 토큰이 없는 경우
            if (!user.hasValidOAuthToken()) {
                return ResponseEntity.ok(Map.of("result", "유효한 OAuth 토큰이 없습니다."));
            }
            
            // OAuth Provider 로그아웃 수행
            Map<String, Object> result = oAuthService.logout(user.getOauthProvider(), user.getOauthAccessToken());
            
            // 사용자의 OAuth 토큰 제거
            user.clearOAuthToken();
            userPort.save(user);
            
            result.put("message", "로그아웃이 완료되었습니다.");
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("OAuth 로그아웃 실패: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of("result", "로그아웃 처리 중 오류가 발생했습니다."));
        }
    }

    @Operation(
        summary = "위치 기반 로그인", 
        description = "사용자 로그인과 동시에 위치 정보를 전달하여 실시간 트리거를 평가하고 알림을 생성합니다."
    )
    @PostMapping("/login/location")
    public ResponseEntity<LoginWithLocationResponse> loginWithLocation(@Valid @RequestBody LoginWithLocationRequest request) {
        log.info("위치 기반 로그인 요청: userId={}, location=[{}, {}]", 
                request.getUserId(), request.getLatitude(), request.getLongitude());

        try {
            // 사용자 인증 확인
            User user = userPort.findByEmail(request.getUserId())
                    .orElse(null);
            
            if (user == null) {
                return ResponseEntity.ok(LoginWithLocationResponse.failure("사용자를 찾을 수 없습니다."));
            }

            // JWT 토큰 생성
            String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

            // 위치 기반 트리거 평가
            LocationTriggerCommand triggerCommand = LocationTriggerCommand.of(
                    request.getUserId(),
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getRadius(),
                    null // 모든 트리거 타입 평가
            );

            TriggerEvaluationResult triggerResult = evaluateTriggerUseCase.evaluateLocationBasedTriggers(triggerCommand);
            TriggerEvaluationResponse triggerResponse = TriggerEvaluationResponse.from(triggerResult);

            return ResponseEntity.ok(LoginWithLocationResponse.success(
                    user.getId(),
                    user.getNickname(),
                    user.getEmail(),
                    accessToken,
                    refreshToken,
                    triggerResponse
            ));

        } catch (Exception e) {
            log.error("위치 기반 로그인 실패: userId={}, error={}", request.getUserId(), e.getMessage(), e);
            return ResponseEntity.ok(LoginWithLocationResponse.failure("로그인 처리 중 오류가 발생했습니다."));
        }
    }

    @Operation(
        summary = "OAuth 연결 해제", 
        description = "OAuth 제공자와의 연결을 해제합니다. JWT 토큰으로 사용자를 조회하여 저장된 OAuth AccessToken을 사용합니다."
    )
    @PostMapping("/oauth/unlink")
    public ResponseEntity<Map<String, Object>> oauthUnlink(
            @Parameter(description = "사용자 JWT 토큰") @RequestHeader("Authorization") String authHeader) {
        
        try {
            // JWT 토큰에서 Bearer 제거
            String jwtToken = authHeader.replace("Bearer ", "");
            
            // JWT에서 사용자 ID 추출
            Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
            
            // 사용자 조회
            User user = userPort.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            
            // OAuth 사용자가 아닌 경우
            if (!user.isOAuthUser()) {
                return ResponseEntity.ok(Map.of("result", "OAuth 사용자가 아닙니다."));
            }
            
            // 유효한 OAuth 토큰이 없는 경우
            if (!user.hasValidOAuthToken()) {
                return ResponseEntity.ok(Map.of("result", "유효한 OAuth 토큰이 없습니다."));
            }
            
            // OAuth Provider 연결 해제 수행
            Map<String, Object> result = oAuthService.unlink(user.getOauthProvider(), user.getOauthAccessToken());
            
            // 사용자 계정 비활성화 또는 삭제
            user.delete(); // 또는 user.deactivate();
            user.clearOAuthToken();
            userPort.save(user);
            
            result.put("message", "연결 해제가 완료되었습니다.");
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("OAuth 연결 해제 실패: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of("result", "연결 해제 처리 중 오류가 발생했습니다."));
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
