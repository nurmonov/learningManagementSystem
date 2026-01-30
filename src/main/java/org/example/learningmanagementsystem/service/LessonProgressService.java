package org.example.learningmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.LessonProgressDTO;
import org.example.learningmanagementsystem.entity.LessonProgress;
import org.example.learningmanagementsystem.repo.LessonProgressRepository;
import org.example.learningmanagementsystem.repo.LessonRepository;
import org.example.learningmanagementsystem.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LessonProgressService {

    private final LessonProgressRepository repository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    public LessonProgressDTO markCompleted(Integer userId, Integer lessonId) {

        LessonProgress progress = repository
                .findByUserIdAndLessonId(userId, lessonId)
                .orElse(new LessonProgress(
                        userRepository.findById(userId).orElseThrow(),
                        lessonRepository.findById(lessonId).orElseThrow())
                        );

        progress.setCompleted(true);
        progress.setCompletedAt(Instant.now());

        repository.save(progress);

        return toDto(progress);
    }

    private LessonProgressDTO toDto(LessonProgress p) {
        LessonProgressDTO dto = new LessonProgressDTO();
        dto.setId(p.getId());
        dto.setUserId(p.getUser().getId());
        dto.setLessonId(p.getLesson().getId());
        dto.setCompleted(p.isCompleted());
        return dto;
    }
}

