package org.example.learningmanagementsystem.mapper;


import org.example.learningmanagementsystem.dto.EnrollmentDTO;
import org.example.learningmanagementsystem.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper( componentModel = "spring")
public interface EnrollmentMapper {

    Enrollment toEntity(EnrollmentDTO dto);

    EnrollmentDTO toDto(Enrollment entity);

    void updateFromDto(EnrollmentDTO dto, @MappingTarget Enrollment enrollment);
}
