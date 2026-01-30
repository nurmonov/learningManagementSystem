package org.example.learningmanagementsystem.mapper;


import org.example.learningmanagementsystem.dto.CourseDTO;
import org.example.learningmanagementsystem.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    Course toEntity(CourseDTO dto);

    CourseDTO toDto(Course entity);

    void updateCourseFromDto(CourseDTO dto, @MappingTarget Course course);
}
