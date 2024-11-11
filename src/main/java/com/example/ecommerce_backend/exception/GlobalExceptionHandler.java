package com.example.ecommerce_backend.exception;

import com.example.ecommerce_backend.api.dto.ExceptionResponseDto;
import com.example.ecommerce_backend.api.dto.ValidationErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<String> errorDetails = e.getBindingResult().getAllErrors()
                .stream()
                .map(objectError -> {
                    String fieldName = ((FieldError) objectError).getField();
                    String errorMessage = objectError.getDefaultMessage();
                    return fieldName + ": " + errorMessage;
                })
                .toList();

        ValidationErrorResponseDto responseDto = new ValidationErrorResponseDto("Validation failed", errorDetails);
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponseDto> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        ExceptionResponseDto responseDto = new ExceptionResponseDto(e.getMessage());
        return new ResponseEntity<>(responseDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleUserNotFoundException(UserNotFoundException e) {
        ExceptionResponseDto responseDto = new ExceptionResponseDto((e.getMessage()));
        return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailFailureException.class)
    public ResponseEntity<ExceptionResponseDto> handleUserNotFoundException(EmailFailureException e) {
        ExceptionResponseDto responseDto = new ExceptionResponseDto((e.getMessage()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<ExceptionResponseDto> handleUserNotVerifiedException(UserNotVerifiedException e) {
        ExceptionResponseDto responseDto = new ExceptionResponseDto((e.getMessage()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
