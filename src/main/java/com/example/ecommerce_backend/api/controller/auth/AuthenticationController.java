package com.example.ecommerce_backend.api.controller.auth;

import com.example.ecommerce_backend.api.dto.ResetPasswordRequestDto;
import com.example.ecommerce_backend.api.dto.UserLoginRequestDto;
import com.example.ecommerce_backend.api.dto.UserLoginResponseDto;
import com.example.ecommerce_backend.api.dto.UserRegistrationDto;
import com.example.ecommerce_backend.model.LocalUser;
import com.example.ecommerce_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        userService.registerUser(userRegistrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> loginUser(@Valid @RequestBody UserLoginRequestDto userLoginRequestDto) {
        String token = userService.loginUser(userLoginRequestDto);
        UserLoginResponseDto userLoginResponseDto = UserLoginResponseDto.builder()
                .jwt(token).build();
        return new ResponseEntity<>(userLoginResponseDto, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<LocalUser> getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user) {
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        if (userService.verifyUser(token)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/forgot")
    public ResponseEntity<Void> forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        userService.resetPassword(resetPasswordRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
