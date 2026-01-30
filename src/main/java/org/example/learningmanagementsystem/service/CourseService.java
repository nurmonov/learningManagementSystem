package org.example.learningmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.CourseDTO;
import org.example.learningmanagementsystem.entity.Course;
import org.example.learningmanagementsystem.mapper.CourseMapper;
import org.example.learningmanagementsystem.repo.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;


    public CourseDTO getCourseById(Integer courseId){
        Optional<Course> course=courseRepository.findById(courseId);
        if(course.isPresent()){
            return courseMapper.toDto(course.get());
        }else {
            throw new RuntimeException("Course not found with id: " + courseId);
        }

    }

    public  List<CourseDTO> getCourses(){
        List<Course> courses=courseRepository.findAll();
        return courses.stream().map(courseMapper::toDto).toList();
    }

    public CourseDTO getCourseByName(String name){
        Optional<Course> course=courseRepository.findByTitle(name);
        if(course.isPresent()){
            return courseMapper.toDto(course.get());
        }
        throw new RuntimeException("Course not found with name: " + name);
    }

    public CourseDTO createCourse(CourseDTO courseDTO){
        Course course=courseMapper.toEntity(courseDTO);
        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    public CourseDTO updateCourse(Integer id, CourseDTO dto) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));


        courseMapper.updateCourseFromDto(dto, course);


        course = courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    public void deleteCourse(Integer id) {
        courseRepository.deleteById(id);
    }


}
