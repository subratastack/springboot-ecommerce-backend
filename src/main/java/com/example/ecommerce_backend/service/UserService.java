package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.api.dto.UserLoginRequestDto;
import com.example.ecommerce_backend.api.dto.UserRegistrationDto;
import com.example.ecommerce_backend.exception.UserAlreadyExistsException;
import com.example.ecommerce_backend.exception.UserNotFoundException;
import com.example.ecommerce_backend.model.LocalUser;

public interface UserService {

    LocalUser registerUser(UserRegistrationDto userRegistrationDto) throws UserAlreadyExistsException;
    String loginUser(UserLoginRequestDto userLoginRequestDto) throws UserNotFoundException;
    Boolean verifyUser(String token);
}
