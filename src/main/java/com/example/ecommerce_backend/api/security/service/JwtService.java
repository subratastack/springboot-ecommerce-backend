package com.example.ecommerce_backend.api.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.ecommerce_backend.model.LocalUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;

    private Algorithm algorithm;
    private static final String USERNAME_KEY = "USERNAME";
    private static final String VERIFICATION_EMAIL_KEY = "VERIFICATION_EMAIL";
    private static final String RESET_PASSWORD_KEY = "RESET_PASSWORD_KEY";

    @PostConstruct
    public void postConstruct() {
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    public String generateJwtToken(LocalUser user) {
        return JWT.create()
                .withClaim(USERNAME_KEY, user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String generateVerificationJwt(LocalUser user) {
        return JWT.create()
                .withClaim(VERIFICATION_EMAIL_KEY, user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String generatePasswordResetJwt(LocalUser user) {
        return JWT.create()
                .withClaim(RESET_PASSWORD_KEY, user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * 60 * 15))) // 15 min
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String getUsername(String token) {
        verifyToken(token);
        return JWT.decode(token).getClaim(USERNAME_KEY).asString();
    }

    public String getResetPasswordEmail(String token) {
        verifyToken(token);
        return JWT.decode(token).getClaim(RESET_PASSWORD_KEY).asString();
    }

    public boolean isTokenExpired(String token) {
        verifyToken(token);
        return JWT.decode(token).getExpiresAt().before(new Date());
    }

    private void verifyToken(String token) throws JWTVerificationException {
        DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
    }
}
