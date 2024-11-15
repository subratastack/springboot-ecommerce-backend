package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.exception.EmailFailureException;
import com.example.ecommerce_backend.model.LocalUser;
import com.example.ecommerce_backend.model.VerificationToken;

public interface EmailService {

    void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException;
    void sendResetPasswordEmail(LocalUser user, String token) throws EmailFailureException;
}
