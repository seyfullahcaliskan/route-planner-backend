package com.routeplanner.backend.exception;

public class OAuthVerificationException extends RuntimeException {
    public OAuthVerificationException(String message) {
        super(message);
    }
}