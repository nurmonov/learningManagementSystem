package org.example.learningmanagementsystem.exseption;


public class InvalidCredentialsException extends RuntimeException {

    // Konstruktor
    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}

