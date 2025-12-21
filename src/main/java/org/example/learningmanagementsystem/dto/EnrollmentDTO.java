package org.example.learningmanagementsystem.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class EnrollmentDTO {

    private Integer courseId;
    private Integer userId;
    private Integer progress;
    private Instant enrolledAt;
}
