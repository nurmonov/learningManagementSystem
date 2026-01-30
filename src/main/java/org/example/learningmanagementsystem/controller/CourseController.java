package org.example.learningmanagementsystem.controller;


import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.CourseDTO;
import org.example.learningmanagementsystem.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/course")
public class CourseController {


    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses(){
        List<CourseDTO> courseDTOS=courseService.getCourses();
        return new ResponseEntity<>(courseDTOS, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable Integer id) {
        CourseDTO courseDTO=courseService.getCourseById(id);
        return new ResponseEntity<>(courseDTO, HttpStatus.OK);
    }


    @GetMapping("/{name}")
    public ResponseEntity<CourseDTO> getCourseByName(@PathVariable String name) {
        CourseDTO courseDTO=courseService.getCourseByName(name);
        return new ResponseEntity<>(courseDTO, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO){
        CourseDTO course=courseService.createCourse(courseDTO);
        return new ResponseEntity<>(course, HttpStatus.OK);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Integer id,@RequestBody CourseDTO courseDTO){
        CourseDTO course=courseService.updateCourse(id,courseDTO);
        return new ResponseEntity<>(courseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Integer id){
        courseService.deleteCourse(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
