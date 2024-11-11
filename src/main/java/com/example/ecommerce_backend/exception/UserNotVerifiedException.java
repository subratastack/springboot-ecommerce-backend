package com.example.ecommerce_backend.exception;

import lombok.Getter;

@Getter
public class UserNotVerifiedException extends RuntimeException {

    private final boolean newEmailSent;

    public UserNotVerifiedException(String message, boolean newEmailSent) {
        super(message);
        this.newEmailSent = newEmailSent;
    }
}
