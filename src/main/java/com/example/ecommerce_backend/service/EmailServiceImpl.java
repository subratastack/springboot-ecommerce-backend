package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.exception.EmailFailureException;
import com.example.ecommerce_backend.model.LocalUser;
import com.example.ecommerce_backend.model.VerificationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${email.from}")
    private String fromAddress;

    @Value("${app.frontend.url}")
    private String url;

    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    private SimpleMailMessage createMailMessage() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromAddress);
        return simpleMailMessage;
    }

    @Override
    public void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException {
        SimpleMailMessage message = createMailMessage();
        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Verify your email to active your account.");
        message.setText("Please follow the link below to verify your email to active your account. \n" +
                url + "/auth/verify?token=" + verificationToken.getToken());

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new EmailFailureException(e.getMessage());
        }
    }

    @Override
    public void sendResetPasswordEmail(LocalUser user, String token) throws EmailFailureException {
        SimpleMailMessage message = createMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Reset your password for you account");
        message.setText("Please follow the link below to reset your password for your account. \n" +
                url + "/auth/reset?token=" + token);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new EmailFailureException(e.getMessage());
        }
    }
}
