package com.example.bankcards.exception;

public class AccessDeniedRuntimeException extends RuntimeException {
    public AccessDeniedRuntimeException(String message) {
        super(message);
    }
}
