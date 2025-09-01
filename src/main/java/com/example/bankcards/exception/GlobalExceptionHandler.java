package com.example.bankcards.exception;

import com.example.bankcards.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("User Not Found")
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCardNotFoundException(
            CardNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Card Not Found")
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<ErrorResponse> handleTransactionException(
            TransactionException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Transaction Error")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(
            InsufficientFundsException ex, HttpServletRequest request) {
        Map<String, String> details = new HashMap<>();
        if (ex.getAvailableBalance() != null && ex.getRequestedAmount() != null) {
            details.put("availableBalance", ex.getAvailableBalance().toString());
            details.put("requestedAmount", ex.getRequestedAmount().toString());
        }
        
        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Insufficient Funds")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .details(details)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CardExpiredException.class)
    public ResponseEntity<ErrorResponse> handleCardExpiredException(
            CardExpiredException ex, HttpServletRequest request) {
        Map<String, String> details = new HashMap<>();
        if (ex.getExpiryDate() != null) {
            details.put("expiryDate", ex.getExpiryDate().toString());
        }
        
        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Card Expired")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .details(details)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Access Denied")
                .status(HttpStatus.FORBIDDEN.value())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .message("Invalid username or password")
                .error("Authentication Failed")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
            UsernameNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("User Not Found")
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> details = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            details.put(fieldName, errorMessage);
        });

        ErrorResponse error = ErrorResponse.builder()
                .message("Validation failed")
                .error("Validation Error")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .details(details)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> details = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            details.put(fieldName, errorMessage);
        });

        ErrorResponse error = ErrorResponse.builder()
                .message("Validation failed")
                .error("Validation Error")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .details(details)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .message("An unexpected error occurred")
                .error("Internal Server Error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
