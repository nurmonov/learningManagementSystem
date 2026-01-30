package org.example.learningmanagementsystem.service;


import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.LessonDTO;
import org.example.learningmanagementsystem.entity.Lesson;
import org.example.learningmanagementsystem.mapper.LessonMapper;
import org.example.learningmanagementsystem.repo.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;


    public LessonDTO getLessonById(int id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));
        return lessonMapper.toDto(lesson);

    }
    public List<LessonDTO> getAllLessons() {
        List<Lesson> lessons = lessonRepository.findAll();
        return lessons.stream()
                .map(lessonMapper::toDto)
                .toList();
    }

    public LessonDTO createLesson(LessonDTO lessonDTO) {
        Lesson lesson = lessonMapper.toEntity(lessonDTO);
        lesson = lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    public LessonDTO updateLessonById(Integer id, LessonDTO lessonDTO) {
        Lesson lesson=lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));

          lessonMapper.updateLesson(lessonDTO, lesson);
          return lessonMapper.toDto(lesson);
    }
    public void deleteLessonById(int id) {
        lessonRepository.deleteById(id);
    }
}
