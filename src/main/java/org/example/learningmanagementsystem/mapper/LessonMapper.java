package org.example.learningmanagementsystem.mapper;


import org.example.learningmanagementsystem.dto.LessonDTO;
import org.example.learningmanagementsystem.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    Lesson toEntity(LessonDTO dto);

    LessonDTO toDto(Lesson entity);

    void updateLesson(LessonDTO dto, @MappingTarget Lesson lesson);
}
