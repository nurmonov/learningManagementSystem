package org.example.learningmanagementsystem.mapper;


import org.example.learningmanagementsystem.dto.LessonDTO;
import org.example.learningmanagementsystem.dto.UserDTO;
import org.example.learningmanagementsystem.entity.Lesson;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    Lesson toEntity(LessonDTO dto);

    LessonDTO toDto(Lesson entity);
}
