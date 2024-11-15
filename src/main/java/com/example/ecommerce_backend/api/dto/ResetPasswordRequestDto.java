package com.example.ecommerce_backend.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordRequestDto {

    @NotNull
    @NotBlank
    private String token;

    @NotNull
    @NotBlank
    @Size(min = 4)
    private String password;
}
