package org.example.learningmanagementsystem.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseDTO {
    private Integer id;
    private String title;
    private String description;
    private String instructor;
    private String duration;
    private Integer students;
    private BigDecimal rating;
    private String image;
    private String level;
    private String category;
    private boolean enrolled;
    private Integer progress;
}
