package org.example.learningmanagementsystem.exseption;


import org.example.learningmanagementsystem.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =  LoggerFactory.getLogger(AuthService.class);

    // UserNotFoundException uchun
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found", ex.getMessage());
    }

    // InvalidCredentialsException uchun
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials", ex.getMessage());
    }

    // Boshqa umumiy xatoliklar uchun
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        // Xatolikni loglash
        logger.error("General exception occurred: {}", ex.getMessage(), ex);

        // Xatolikni qaytarish
        ErrorResponse errorResponse = new ErrorResponse(
                "error",
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Boshqa exceptionlarni avtomatik qayta ishlash uchun umumiy metod
    private ResponseEntity<?> buildErrorResponse(HttpStatus status, String error, String message) {
        // Hozirgi vaqtni olish
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // ErrorResponse obyektini yaratish
        ErrorResponse errorResponse = new ErrorResponse(
                "error",          // status
                error,            // error
                message,          // message
                timestamp         // timestamp
        );

        // Javobni qaytarish
        return new ResponseEntity<>(errorResponse, status);
    }
}
