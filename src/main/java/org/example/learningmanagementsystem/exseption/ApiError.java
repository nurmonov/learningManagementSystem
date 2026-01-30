package org.example.learningmanagementsystem.exseption;




import lombok.Data;

@Data
public class ApiError {
    private String status;
    private String errorCode;
    private String message;
    private String timestamp;

    public ApiError(String status, String errorCode, String message, String timestamp) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = timestamp;
    }
}
