package org.example.learningmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.EnrollmentDTO;
import org.example.learningmanagementsystem.entity.Course;
import org.example.learningmanagementsystem.entity.Enrollment;
import org.example.learningmanagementsystem.entity.User;
import org.example.learningmanagementsystem.mapper.EnrollmentMapper;
import org.example.learningmanagementsystem.repo.CourseRepository;
import org.example.learningmanagementsystem.repo.EnrollmentRepository;
import org.example.learningmanagementsystem.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;

    // CREATE (enroll)
    public EnrollmentDTO enroll(EnrollmentDTO dto) {

        if (enrollmentRepository.existsByUserIdAndCourseId(dto.getUserId(), dto.getCourseId())) {
            throw new RuntimeException("User already enrolled");
        }

        User user = userRepository.findById(dto.getUserId()).orElseThrow();
        Course course = courseRepository.findById(dto.getCourseId()).orElseThrow();

        Enrollment enrollment = enrollmentMapper.toEntity(dto);
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setProgress(0);

        enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDto(enrollment);
    }


    public List<EnrollmentDTO> getUserEnrollments(Integer userId) {
        return enrollmentRepository.findById(userId)
                .stream()
                .map(enrollmentMapper::toDto)
                .toList();
    }


    public EnrollmentDTO update(Integer id, EnrollmentDTO dto) {

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollmentMapper.updateFromDto(dto, enrollment);

        enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDto(enrollment);
    }


    public void delete(Integer id) {
        Enrollment enrollment= enrollmentRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Enrollment not found by "+id) );
        enrollmentRepository.delete(enrollment);
    }
}

