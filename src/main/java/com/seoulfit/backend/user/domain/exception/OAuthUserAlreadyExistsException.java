package com.seoulfit.backend.user.domain.exception;

public class OAuthUserAlreadyExistsException extends RuntimeException {
    public OAuthUserAlreadyExistsException(String message) {
        super(message);
    }
}
