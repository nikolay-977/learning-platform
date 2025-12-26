package ru.skillfactory.learning.platform.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.skillfactory.learning.platform.dto.request.CreateCourseRequest;
import ru.skillfactory.learning.platform.dto.request.CreateUserRequest;
import ru.skillfactory.learning.platform.dto.request.EnrollRequest;
import ru.skillfactory.learning.platform.dto.response.CourseResponse;
import ru.skillfactory.learning.platform.dto.response.EnrollmentResponse;
import ru.skillfactory.learning.platform.dto.response.UserResponse;
import ru.skillfactory.learning.platform.entity.Category;
import ru.skillfactory.learning.platform.repository.*;
import ru.skillfactory.learning.platform.service.CourseService;
import ru.skillfactory.learning.platform.service.EnrollmentService;
import ru.skillfactory.learning.platform.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserCourseIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        // Очищаем базу данных в обратном порядке зависимостей
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testUserEnrollInCourse_Integration() {
        // 1. Создаем категорию
        Category category = new Category();
        category.setName("Programming");
        Category savedCategory = categoryRepository.save(category);

        // 2. Создаем преподавателя
        CreateUserRequest teacherRequest = new CreateUserRequest();
        teacherRequest.setName("John Teacher");
        teacherRequest.setEmail("teacher@example.com");
        teacherRequest.setRole("TEACHER");
        UserResponse teacher = userService.createUser(teacherRequest);

        // 3. Создаем студента
        CreateUserRequest studentRequest = new CreateUserRequest();
        studentRequest.setName("Jane Student");
        studentRequest.setEmail("student@example.com");
        studentRequest.setRole("STUDENT");
        UserResponse student = userService.createUser(studentRequest);

        // 4. Создаем курс
        CreateCourseRequest courseRequest = new CreateCourseRequest();
        courseRequest.setTitle("Java Programming");
        courseRequest.setDescription("Learn Java from scratch");
        courseRequest.setTeacherId(teacher.getId());
        courseRequest.setCategoryId(savedCategory.getId());
        courseRequest.setStartDate(LocalDate.now().plusDays(1));
        CourseResponse course = courseService.createCourse(courseRequest);

        // 5. Записываем студента на курс
        EnrollRequest enrollRequest = new EnrollRequest();
        enrollRequest.setStudentId(student.getId());
        enrollRequest.setCourseId(course.getId());
        EnrollmentResponse enrollment = enrollmentService.enrollStudent(enrollRequest);

        // 6. Проверяем результаты
        assertNotNull(enrollment);
        assertEquals(student.getId(), enrollment.getStudentId());
        assertEquals(course.getId(), enrollment.getCourseId());

        // 7. Проверяем, что студент действительно записан
        boolean isEnrolled = enrollmentService.isStudentEnrolled(student.getId(), course.getId());
        assertTrue(isEnrolled);

        // 8. Проверяем список курсов студента
        var enrolledCourses = courseService.getEnrolledCourses(student.getId());
        assertEquals(1, enrolledCourses.size());
        assertEquals("Java Programming", enrolledCourses.get(0).getTitle());
    }
}