package org.example.learningmanagementsystem.repo;

import org.example.learningmanagementsystem.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson,Integer> {
}
