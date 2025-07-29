package com.seoulfit.backend.application.exception;

public class OAuthUserNotFoundException extends RuntimeException {
    public OAuthUserNotFoundException(String message) {
        super(message);
    }
}
