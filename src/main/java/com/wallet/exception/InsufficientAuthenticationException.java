package com.wallet.exception;

public class InsufficientAuthenticationException extends RuntimeException {
    public InsufficientAuthenticationException(String message) {
        super(message);
    }
}
