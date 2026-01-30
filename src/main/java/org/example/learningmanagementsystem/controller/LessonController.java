package org.example.learningmanagementsystem.controller;


import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.LessonDTO;
import org.example.learningmanagementsystem.entity.Lesson;
import org.example.learningmanagementsystem.service.LessonService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/lesson")
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    public HttpEntity<List<LessonDTO>> getAllLessons() {
        List<LessonDTO> lessons = lessonService.getAllLessons();
        return new ResponseEntity<>(lessons, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public HttpEntity<LessonDTO> getLessonById(@PathVariable int id) {
        LessonDTO lesson = lessonService.getLessonById(id);
        return new ResponseEntity<>(lesson, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public HttpEntity<LessonDTO> createLesson(@RequestBody LessonDTO lessonDTO) {
        LessonDTO lesson = lessonService.createLesson(lessonDTO);
        return new ResponseEntity<>(lesson, HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public HttpEntity<LessonDTO> updateLesson(@RequestBody LessonDTO lessonDTO, @PathVariable Integer id) {
        LessonDTO lesson = lessonService.updateLessonById(id, lessonDTO);
        return new ResponseEntity<>(lesson, HttpStatus.OK);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public HttpEntity<LessonDTO> deleteLessonById(@RequestParam Integer id) {
        lessonService.deleteLessonById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
