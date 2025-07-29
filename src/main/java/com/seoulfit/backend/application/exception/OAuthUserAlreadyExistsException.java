package com.seoulfit.backend.application.exception;

public class OAuthUserAlreadyExistsException extends RuntimeException {
    public OAuthUserAlreadyExistsException(String message) {
        super(message);
    }
}
