package com.example.ecommerce_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@Table(name = "local_user")
@NoArgsConstructor
@AllArgsConstructor
public class LocalUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = false, length = 1000)
    private String password;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Builder.Default
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Address> addresses = new LinkedHashSet<>();

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id desc")
    private Set<VerificationToken> verificationTokens = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResetPasswordToken> resetPasswordTokens = new ArrayList<>();

}