package org.example.learningmanagementsystem.dto;

import lombok.Data;

@Data
public class LessonDTO {
    private Integer id;
    private String title;
    private String duration;
    private String type;
    private boolean completed;
    private boolean locked;
}
