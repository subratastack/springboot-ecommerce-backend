package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.api.dto.UserLoginRequestDto;
import com.example.ecommerce_backend.api.dto.UserRegistrationDto;
import com.example.ecommerce_backend.api.security.service.EncryptionService;
import com.example.ecommerce_backend.api.security.service.JwtService;
import com.example.ecommerce_backend.exception.UserAlreadyExistsException;
import com.example.ecommerce_backend.exception.UserNotFoundException;
import com.example.ecommerce_backend.model.LocalUser;
import com.example.ecommerce_backend.model.repository.LocalUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    LocalUserRepository localUserRepository;

    @Mock
    EncryptionService encryptionService;

    @Mock
    JwtService jwtService;

    @InjectMocks
    UserServiceImpl userService;

    private UserRegistrationDto dto;
    private LocalUser user;

    @BeforeEach
    void setUp() {
        dto = UserRegistrationDto.builder()
                .email("test@test.com")
                .firstName("test")
                .lastName("user")
                .password("123")
                .username("test")
                .build();

        user = LocalUser.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(dto.getUsername())
                .build();
    }

    @Test
    void registerUser_shouldCreateNewUser() {

        when(localUserRepository.save(any(LocalUser.class))).thenReturn(user);

        LocalUser registeredUser = userService.registerUser(dto);

        assertNotNull(registeredUser);
        assertEquals(dto.getUsername(), registeredUser.getUsername());
    }

    @Test
    void registerUser_shouldThrowUserAlreadyExistsException() {


        when(localUserRepository.findByEmailIgnoreCase(any(String.class))).thenReturn(Optional.of(user));
        when(localUserRepository.findByUsernameIgnoreCase(any(String.class))).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> {
           userService.registerUser(dto);
        });
    }

    @Test
    void loginUser_shouldReturnJwtToken() {

        final String JWT_TOKEN = "JWT_TOKEN";

        UserLoginRequestDto userLoginRequestDto = UserLoginRequestDto.builder()
                .username("user")
                .password("pass")
                .build();

        when(localUserRepository.findByUsernameIgnoreCase(any(String.class))).thenReturn(Optional.of(user));
        when(encryptionService.verifyPassword(any(String.class), any(String.class))).thenReturn(true);
        when(jwtService.generateJwtToken(any(LocalUser.class))).thenReturn(JWT_TOKEN);

        String token = userService.loginUser(userLoginRequestDto);

        assertNotNull(token);
        assertEquals(JWT_TOKEN, token);
    }

    @Test
    void loginUser_shouldThrowUserNotFoundException() {
        UserLoginRequestDto userLoginRequestDto = UserLoginRequestDto.builder()
                .username("user")
                .password("pass")
                .build();

        when(localUserRepository.findByUsernameIgnoreCase(any(String.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
           userService.loginUser(userLoginRequestDto);
        });
    }
}