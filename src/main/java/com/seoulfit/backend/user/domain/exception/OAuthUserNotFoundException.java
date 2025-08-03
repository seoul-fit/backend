package com.seoulfit.backend.user.domain.exception;

public class OAuthUserNotFoundException extends RuntimeException {
    public OAuthUserNotFoundException(String message) {
        super(message);
    }
}
