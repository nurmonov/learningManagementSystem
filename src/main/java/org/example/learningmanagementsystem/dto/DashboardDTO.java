package org.example.learningmanagementsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardDTO {

    private List<CourseDTO> enrolledCourses;
}