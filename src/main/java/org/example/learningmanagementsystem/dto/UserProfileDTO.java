package org.example.learningmanagementsystem.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class UserProfileDTO {
    private Integer id;
    private String username;
    private String email;
    private Instant createdAt;
}
