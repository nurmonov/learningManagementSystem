package org.example.learningmanagementsystem.exseption;


public class InvalidCredentialsException extends BaseException {

    public InvalidCredentialsException(String message) {
        super(message, "INVALID_CREDENTIALS");
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, "INVALID_CREDENTIALS", cause);
    }
}