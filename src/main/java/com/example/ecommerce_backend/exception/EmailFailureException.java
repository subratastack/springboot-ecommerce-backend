package com.example.ecommerce_backend.exception;

public class EmailFailureException extends RuntimeException {
    public EmailFailureException(String message) {
        super(message);
    }
}
