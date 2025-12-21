package org.example.learningmanagementsystem.repo;

import org.example.learningmanagementsystem.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonProgressRepository  extends JpaRepository<LessonProgress,Integer> {
}
