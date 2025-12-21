package org.example.learningmanagementsystem.exseption;


public class UserNotFoundException extends RuntimeException {

    // Konstruktor
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

