package org.example.learningmanagementsystem.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private String tokenType = "Bearer";
    private String refreshToken;
}