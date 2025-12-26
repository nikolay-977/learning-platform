package ru.skillfactory.learning.platform.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.skillfactory.learning.platform.dto.request.*;
import ru.skillfactory.learning.platform.dto.response.*;
import ru.skillfactory.learning.platform.entity.Category;
import ru.skillfactory.learning.platform.repository.*;
import ru.skillfactory.learning.platform.service.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class AssignmentSubmissionIntegrationTest extends BaseIntegrationTest{

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        // Очищаем базу данных в обратном порядке зависимостей
        submissionRepository.deleteAll();
        assignmentRepository.deleteAll();
        lessonRepository.deleteAll();
        moduleRepository.deleteAll();
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testCompleteAssignmentWorkflow_Integration() {
        // 1. Создаем категорию
        Category category = new Category();
        category.setName("Programming");
        Category savedCategory = categoryRepository.save(category);

        // 2. Создаем преподавателя
        var teacherRequest = new CreateUserRequest();
        teacherRequest.setName("Professor X");
        teacherRequest.setEmail("professor@example.com");
        teacherRequest.setRole("TEACHER");
        UserResponse teacher = userService.createUser(teacherRequest);

        // 3. Создаем студента
        var studentRequest = new CreateUserRequest();
        studentRequest.setName("Student Y");
        studentRequest.setEmail("student@example.com");
        studentRequest.setRole("STUDENT");
        UserResponse student = userService.createUser(studentRequest);

        // 4. Создаем курс
        var courseRequest = new CreateCourseRequest();
        courseRequest.setTitle("Advanced Java");
        courseRequest.setDescription("Advanced Java concepts");
        courseRequest.setTeacherId(teacher.getId());
        courseRequest.setCategoryId(savedCategory.getId());
        CourseResponse course = courseService.createCourse(courseRequest);

        // 5. Создаем модуль
        var moduleRequest = new CreateModuleRequest();
        moduleRequest.setTitle("Collections Framework");
        moduleRequest.setCourseId(course.getId());
        ModuleResponse module = moduleService.createModule(moduleRequest);

        // 6. Создаем урок
        var lessonRequest = new CreateLessonRequest();
        lessonRequest.setTitle("ArrayList vs LinkedList");
        lessonRequest.setContent("Comparison of list implementations");
        lessonRequest.setModuleId(module.getId());
        LessonResponse lesson = lessonService.createLesson(lessonRequest);

        // 7. Создаем задание
        var assignmentRequest = new CreateAssignmentRequest();
        assignmentRequest.setTitle("Implement custom ArrayList");
        assignmentRequest.setDescription("Create your own ArrayList implementation");
        assignmentRequest.setLessonId(lesson.getId());
        assignmentRequest.setDueDate(LocalDate.now().plusDays(14));
        assignmentRequest.setMaxScore(100);
        AssignmentResponse assignment = assignmentService.createAssignment(assignmentRequest);

        // 8. Студент отправляет решение
        var submitRequest = new SubmitAssignmentRequest();
        submitRequest.setAssignmentId(assignment.getId());
        submitRequest.setContent("Here is my ArrayList implementation...");
        SubmissionResponse submission = submissionService.submitAssignment(student.getId(), submitRequest);

        // 9. Проверяем отправку
        assertNotNull(submission);
        assertEquals(assignment.getId(), submission.getAssignmentId());
        assertEquals(student.getId(), submission.getStudentId());

        // 10. Проверяем, что решение действительно отправлено
        boolean hasSubmitted = submissionService.hasStudentSubmitted(student.getId(), assignment.getId());
        assertTrue(hasSubmitted);

        // 11. Преподаватель оценивает работу
        var gradedSubmission = submissionService.gradeSubmission(submission.getId(), 85, "Good implementation!");

        // 12. Проверяем оценку
        assertNotNull(gradedSubmission.getScore());
        assertEquals(85, gradedSubmission.getScore());
        assertEquals("Good implementation!", gradedSubmission.getFeedback());

        // 13. Проверяем список решений по заданию
        var submissions = submissionService.getSubmissionsByAssignment(assignment.getId());
        assertEquals(1, submissions.size());

        // 14. Проверяем список решений студента
        var studentSubmissions = submissionService.getSubmissionsByStudent(student.getId());
        assertEquals(1, studentSubmissions.size());
    }
}