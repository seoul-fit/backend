package com.seoulfit.backend.shared.exception;

/**
 * 인증이 필요한 리소스에 대한 인증되지 않은 접근 시 발생하는 예외
 * 
 * HTTP 401 Unauthorized 상태 코드를 반환합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
public class UnauthorizedException extends BusinessException {

    /**
     * 기본 인증 실패 예외를 생성합니다.
     */
    public UnauthorizedException() {
        super("UNAUTHORIZED", "인증이 필요합니다.", 401);
    }

    /**
     * 커스텀 메시지로 인증 실패 예외를 생성합니다.
     * 
     * @param message 커스텀 메시지
     */
    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message, 401);
    }

    /**
     * 토큰 관련 인증 실패 예외를 생성합니다.
     * 
     * @param tokenType 토큰 타입 (예: "JWT", "OAuth")
     * @return 토큰 관련 예외
     */
    public static UnauthorizedException invalidToken(String tokenType) {
        return new UnauthorizedException(String.format("유효하지 않은 %s 토큰입니다.", tokenType));
    }

    /**
     * 만료된 토큰 예외를 생성합니다.
     * 
     * @return 토큰 만료 예외
     */
    public static UnauthorizedException expiredToken() {
        return new UnauthorizedException("토큰이 만료되었습니다. 다시 로그인해주세요.");
    }

    /**
     * 권한 부족 예외를 생성합니다.
     * 
     * @return 권한 부족 예외
     */
    public static UnauthorizedException insufficientPermissions() {
        return new UnauthorizedException("해당 리소스에 접근할 권한이 없습니다.");
    }
}