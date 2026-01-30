package org.example.learningmanagementsystem.repo;


import org.example.learningmanagementsystem.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course,Integer> {

    Optional<Course> findByTitle(String title);
}
