package org.example.learningmanagementsystem.repo;

import org.example.learningmanagementsystem.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ModuleRepository extends JpaRepository<Module,Integer> {

//    @Query("""
//   select m from Module m
//   left join fetch m.lessons
//   where m.course.id = :courseId
//""")
//    List<Module> findWithLessonsByCourseId(Integer courseId);

}
