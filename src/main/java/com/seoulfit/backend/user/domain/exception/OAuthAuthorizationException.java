package com.seoulfit.backend.user.domain.exception;

/**
 * OAuth 인가코드 검증 예외
 * 인가코드 검증 과정에서 발생하는 예외를 처리
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
public class OAuthAuthorizationException extends OAuthException {

    public OAuthAuthorizationException(String message) {
        super(message);
    }

    public OAuthAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
