//package org.example.learningmanagementsystem.dataLoader;
//
//
//import lombok.RequiredArgsConstructor;
//import org.example.learningmanagementsystem.entity.*;
//import org.example.learningmanagementsystem.entity.Module;
//import org.example.learningmanagementsystem.entity.roles.Level;
//import org.example.learningmanagementsystem.entity.roles.LessonType;
//import org.example.learningmanagementsystem.entity.roles.Role;
//import org.example.learningmanagementsystem.repo.*;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class DatabaseSeeder implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final CourseRepository courseRepository;
//    private final ModuleRepository moduleRepository;
//    private final LessonRepository lessonRepository;
//    private final EnrollmentRepository enrollmentRepository;
//    private final LessonProgressRepository lessonProgressRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) throws Exception {
//
//        // ====== Users ======
//        List<User> users = new ArrayList<>();
//
//        for (int i = 1; i <= 5; i++) {
//            User user = new User();
//            user.setUsername("user" + i);
//            user.setEmail("user" + i + "@example.com");
//
//            // 🔐 password encoder bilan hash qilish
//            String encodedPassword = passwordEncoder.encode("password" + i);
//            user.setPasswordHash(encodedPassword);
//
//            user.setRole(i % 2 == 0 ? Role.ADMIN : Role.USER);
//            user.setCreatedAt(Instant.now());
//
//            users.add(user);
//        }
//
//        userRepository.saveAll(users);
//
//
//        // ====== Courses ======
//        List<Course> courses = new ArrayList<>();
//        for (int i = 1; i <= 5; i++) {
//            Course course = new Course();
//            course.setTitle("Course " + i);
//            course.setDescription("Description for Course " + i);
//            course.setInstructor("Instructor " + i);
//            course.setDuration(i * 10 + "h");
//            course.setStudents(i * 5);
//            course.setRating(BigDecimal.valueOf(4 + i * 0.1));
//            course.setImage("course" + i + ".png");
//            course.setLevel(Level.Beginner);
//            course.setCategory("Category " + i);
//            courses.add(course);
//        }
//        courseRepository.saveAll(courses);
//
//        // ====== Modules ======
//        List<Module> modules = new ArrayList<>();
//        int counter = 1;
//        for (Course course : courses) {
//            Module module = new Module();
//            module.setCourse(course);
//            module.setTitle("Module " + counter);
//            module.setPosition(counter);
//            modules.add(module);
//            counter++;
//        }
//        moduleRepository.saveAll(modules);
//
//        // ====== Lessons ======
//        List<Lesson> lessons = new ArrayList<>();
//        counter = 1;
//        for (Module module : modules) {
//            Lesson lesson = new Lesson();
//            lesson.setModule(module);
//            lesson.setTitle("Lesson " + counter);
//            lesson.setDuration("30 min");
//            lesson.setType(LessonType.VIDEO);
//            lesson.setPosition(counter);
//            lesson.setLocked(false);
//            lessons.add(lesson);
//            counter++;
//        }
//        lessonRepository.saveAll(lessons);
//
//        // ====== Enrollments ======
//        List<Enrollment> enrollments = new ArrayList<>();
//        for (int i = 0; i < users.size(); i++) {
//            Enrollment enrollment = new Enrollment();
//            enrollment.setUser(users.get(i));
//            enrollment.setCourse(courses.get(i));
//            enrollment.setProgress(i * 20);
//            enrollment.setEnrolledAt(Instant.now());
//            enrollments.add(enrollment);
//        }
//        enrollmentRepository.saveAll(enrollments);
//
//        // ====== LessonProgress ======
//        List<LessonProgress> progresses = new ArrayList<>();
//        for (int i = 0; i < users.size(); i++) {
//            LessonProgress progress = new LessonProgress();
//            progress.setUser(users.get(i));
//            progress.setLesson(lessons.get(i));
//            progress.setCompleted(i % 2 == 0);
//            progress.setCompletedAt(i % 2 == 0 ? Instant.now() : null);
//            progresses.add(progress);
//        }
//        lessonProgressRepository.saveAll(progresses);
//
//        System.out.println("✅ Database seeded successfully!");
//    }
//}
