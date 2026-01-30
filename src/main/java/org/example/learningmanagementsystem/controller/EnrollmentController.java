package org.example.learningmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.EnrollmentDTO;
import org.example.learningmanagementsystem.service.EnrollmentService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public EnrollmentDTO enroll(@RequestBody EnrollmentDTO dto) {
        return enrollmentService.enroll(dto);
    }

    @GetMapping("/user/{userId}")
    public HttpEntity<List<EnrollmentDTO>> getUserEnrollments(@PathVariable Integer userId) {
          List<EnrollmentDTO> list = enrollmentService.getUserEnrollments(userId);
          return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public HttpEntity<EnrollmentDTO> update(@PathVariable Integer id,
                                @RequestBody EnrollmentDTO dto) {
        EnrollmentDTO enrollmentDTO= enrollmentService.update(id, dto);
        return new ResponseEntity<>(enrollmentDTO, HttpStatus.OK);
    }



    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public HttpEntity<Void> delete(@PathVariable Integer id) {
        enrollmentService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

