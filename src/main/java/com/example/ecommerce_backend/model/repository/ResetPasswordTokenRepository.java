package com.example.ecommerce_backend.model.repository;

import com.example.ecommerce_backend.model.LocalUser;
import com.example.ecommerce_backend.model.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    Optional<ResetPasswordToken> findByTokenAndUser_Id(String token, Long id);

    void deleteByTokenAndUser(String token, LocalUser user);
}
