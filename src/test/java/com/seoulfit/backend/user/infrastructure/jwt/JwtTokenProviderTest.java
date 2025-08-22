package com.seoulfit.backend.user.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * JwtTokenProvider 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private SecretKey secretKey;
    private static final String SECRET = "test-secret-key-minimum-256-bits-for-hmac-sha256";
    private static final long ACCESS_TOKEN_VALIDITY = 1800; // 30분
    private static final long REFRESH_TOKEN_VALIDITY = 1209600; // 2주

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                SECRET,
                ACCESS_TOKEN_VALIDITY,
                REFRESH_TOKEN_VALIDITY
        );
        secretKey = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    @Test
    @DisplayName("액세스 토큰 생성 - 성공")
    void generateAccessToken_Success() {
        // given
        String userId = "123";

        // when
        String token = jwtTokenProvider.generateAccessToken(userId);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        
        // 토큰 파싱하여 검증
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        assertThat(claims.getSubject()).isEqualTo(userId);
        assertThat(claims.get("type", String.class)).isEqualTo("access");
        assertThat(claims.getIssuedAt()).isBeforeOrEqualTo(new Date());
        assertThat(claims.getExpiration()).isAfter(new Date());
    }

    @Test
    @DisplayName("리프레시 토큰 생성 - 성공")
    void generateRefreshToken_Success() {
        // given
        String userId = "456";

        // when
        String token = jwtTokenProvider.generateRefreshToken(userId);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        
        // 토큰 파싱하여 검증
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        assertThat(claims.getSubject()).isEqualTo(userId);
        assertThat(claims.get("type", String.class)).isEqualTo("refresh");
        assertThat(claims.getIssuedAt()).isBeforeOrEqualTo(new Date());
        assertThat(claims.getExpiration()).isAfter(new Date());
    }

    @Test
    @DisplayName("토큰에서 사용자 ID 추출 - 성공")
    void getUserIdFromToken_Success() {
        // given
        String userId = "789";
        String token = jwtTokenProvider.generateAccessToken(userId);

        // when
        Long extractedUserId = jwtTokenProvider.getUserIdFromToken(token);

        // then
        assertThat(extractedUserId).isEqualTo(789L);
    }

    @Test
    @DisplayName("유효한 토큰 검증 - 성공")
    void validateToken_ValidToken() {
        // given
        String token = jwtTokenProvider.generateAccessToken("123");

        // when
        boolean isValid = jwtTokenProvider.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 토큰 검증 - 실패")
    void validateToken_InvalidToken() {
        // given
        String invalidToken = "invalid.token.here";

        // when
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰 검증 - 실패")
    void validateToken_ExpiredToken() {
        // given
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() - 1000); // 1초 전 만료
        
        String expiredToken = Jwts.builder()
                .subject("123")
                .issuedAt(new Date(now.getTime() - 2000))
                .expiration(expiredDate)
                .signWith(secretKey)
                .compact();

        // when
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("널 토큰 검증 - 실패")
    void validateToken_NullToken() {
        // when
        boolean isValid = jwtTokenProvider.validateToken(null);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("빈 토큰 검증 - 실패")
    void validateToken_EmptyToken() {
        // when
        boolean isValid = jwtTokenProvider.validateToken("");

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("액세스 토큰 여부 확인 - 액세스 토큰")
    void isAccessToken_AccessToken() {
        // given
        String accessToken = jwtTokenProvider.generateAccessToken("123");

        // when
        boolean isAccess = jwtTokenProvider.isAccessToken(accessToken);

        // then
        assertThat(isAccess).isTrue();
    }

    @Test
    @DisplayName("액세스 토큰 여부 확인 - 리프레시 토큰")
    void isAccessToken_RefreshToken() {
        // given
        String refreshToken = jwtTokenProvider.generateRefreshToken("123");

        // when
        boolean isAccess = jwtTokenProvider.isAccessToken(refreshToken);

        // then
        assertThat(isAccess).isFalse();
    }

    @Test
    @DisplayName("리프레시 토큰 여부 확인 - 리프레시 토큰")
    void isRefreshToken_RefreshToken() {
        // given
        String refreshToken = jwtTokenProvider.generateRefreshToken("456");

        // when
        boolean isRefresh = jwtTokenProvider.isRefreshToken(refreshToken);

        // then
        assertThat(isRefresh).isTrue();
    }

    @Test
    @DisplayName("리프레시 토큰 여부 확인 - 액세스 토큰")
    void isRefreshToken_AccessToken() {
        // given
        String accessToken = jwtTokenProvider.generateAccessToken("456");

        // when
        boolean isRefresh = jwtTokenProvider.isRefreshToken(accessToken);

        // then
        assertThat(isRefresh).isFalse();
    }

    @Test
    @DisplayName("레거시 createAccessToken 메서드 - 성공")
    void createAccessToken_Legacy() {
        // given
        Long userId = 123L;
        String email = "test@example.com";

        // when
        String token = jwtTokenProvider.createAccessToken(userId, email);

        // then
        assertThat(token).isNotNull();
        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(123L);
        assertThat(jwtTokenProvider.isAccessToken(token)).isTrue();
    }

    @Test
    @DisplayName("레거시 createRefreshToken 메서드 - 성공")
    void createRefreshToken_Legacy() {
        // given
        Long userId = 456L;

        // when
        String token = jwtTokenProvider.createRefreshToken(userId);

        // then
        assertThat(token).isNotNull();
        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(456L);
        assertThat(jwtTokenProvider.isRefreshToken(token)).isTrue();
    }

    @Test
    @DisplayName("토큰 만료 시간 검증 - 액세스 토큰")
    void accessTokenExpiry_Validation() {
        // given
        String token = jwtTokenProvider.generateAccessToken("123");

        // when
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        long expiryTime = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();

        // then
        // 30분 = 1800초 = 1800000 밀리초 (오차 범위 1초)
        assertThat(expiryTime).isBetween(1799000L, 1801000L);
    }

    @Test
    @DisplayName("토큰 만료 시간 검증 - 리프레시 토큰")
    void refreshTokenExpiry_Validation() {
        // given
        String token = jwtTokenProvider.generateRefreshToken("456");

        // when
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        long expiryTime = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();

        // then
        // 2주 = 1209600초 = 1209600000 밀리초 (오차 범위 1초)
        assertThat(expiryTime).isBetween(1209599000L, 1209601000L);
    }

    @Test
    @DisplayName("다른 시크릿 키로 서명된 토큰 검증 - 실패")
    void validateToken_DifferentSecret() {
        // given
        SecretKey differentKey = Keys.hmacShaKeyFor("different-secret-key-minimum-256-bits-for-test".getBytes());
        String tokenWithDifferentKey = Jwts.builder()
                .subject("123")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(differentKey)
                .compact();

        // when
        boolean isValid = jwtTokenProvider.validateToken(tokenWithDifferentKey);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("토큰 타입이 없는 토큰 검증")
    void tokenWithoutType_Validation() {
        // given
        String tokenWithoutType = Jwts.builder()
                .subject("123")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(secretKey)
                .compact();

        // when
        boolean isValid = jwtTokenProvider.validateToken(tokenWithoutType);
        boolean isAccess = jwtTokenProvider.isAccessToken(tokenWithoutType);
        boolean isRefresh = jwtTokenProvider.isRefreshToken(tokenWithoutType);

        // then
        assertThat(isValid).isTrue();
        assertThat(isAccess).isFalse();
        assertThat(isRefresh).isFalse();
    }
}