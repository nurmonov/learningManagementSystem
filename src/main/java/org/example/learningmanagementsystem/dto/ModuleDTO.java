package org.example.learningmanagementsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class ModuleDTO {
    private Integer id;
    private String title;

    private List<LessonDTO> lessons;
}
