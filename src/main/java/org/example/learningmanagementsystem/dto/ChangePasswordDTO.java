package org.example.learningmanagementsystem.dto;


import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String oldPassword; // Eski parol
    private String newPassword; // Yangi parol
}

