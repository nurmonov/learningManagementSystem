package org.example.learningmanagementsystem.exseption;


public class ResourceConflictException extends BaseException {

    public ResourceConflictException(String message) {
        super(message, "RESOURCE_CONFLICT");
    }

    public ResourceConflictException(String message, Throwable cause) {
        super(message, "RESOURCE_CONFLICT", cause);
    }
}
