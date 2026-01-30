package org.example.learningmanagementsystem.repo;

import org.example.learningmanagementsystem.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment,Integer> {

    boolean existsByUserIdAndCourseId(Integer userId, Integer courseId);



}
