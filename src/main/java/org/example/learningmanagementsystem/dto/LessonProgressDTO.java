package org.example.learningmanagementsystem.dto;

import lombok.Data;

@Data
public class LessonProgressDTO {
    private Integer id;
    private Integer userId;
    private Integer lessonId;
    private boolean completed;
}

