package com.example.ecommerce_backend.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegistrationDto {

    @NotNull
    @NotBlank
    @Size(min = 4)
    @JsonProperty("username")
    private String username;

    @NotBlank
    @NotNull
    @Email
    @JsonProperty("email")
    private String email;

    @NotBlank
    @NotNull
    @Size(min = 4)
    @JsonProperty("password")
    private String password;

    @NotBlank
    @NotNull
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank
    @NotNull
    @JsonProperty("last_name")
    private String lastName;
}
