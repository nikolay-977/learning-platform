package ru.skillfactory.learning.platform.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skillfactory.learning.platform.dto.request.CreateUserRequest;
import ru.skillfactory.learning.platform.dto.request.CreateCategoryRequest;
import ru.skillfactory.learning.platform.dto.request.CreateCourseRequest;
import ru.skillfactory.learning.platform.dto.response.UserResponse;
import ru.skillfactory.learning.platform.dto.response.CategoryResponse;
import ru.skillfactory.learning.platform.dto.response.CourseResponse;
import ru.skillfactory.learning.platform.entity.User;
import ru.skillfactory.learning.platform.entity.Category;
import ru.skillfactory.learning.platform.repository.UserRepository;
import ru.skillfactory.learning.platform.repository.CategoryRepository;
import ru.skillfactory.learning.platform.repository.CourseRepository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemoDataService {

    private final UserService userService;
    private final CategoryService categoryService;
    private final CourseService courseService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    @Value("${app.demo.data.enabled:true}")
    private boolean demoDataEnabled;

    @Value("${app.demo.data.clear:false}")
    private boolean clearExistingData;

    @PostConstruct
    public void init() {
        if (demoDataEnabled) {
            log.info("Initializing demo data...");
            initializeDemoData();
            log.info("Demo data initialization completed");
        } else {
            log.info("Demo data initialization is disabled");
        }
    }

    private void initializeDemoData() {
        if (clearExistingData) {
            clearExistingData();
        }

        if (userRepository.count() == 0) {
            createDemoUsers();
        }

        if (categoryRepository.count() == 0) {
            createDemoCategories();
        }

        if (courseRepository.count() == 0) {
            createDemoCourses();
        }
    }

    private void clearExistingData() {
        log.info("Clearing existing data...");
        courseRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        log.info("Existing data cleared");
    }

    private void createDemoUsers() {
        log.info("Creating demo users...");

        // Создаем преподавателей
        List<CreateUserRequest> teacherRequests = List.of(
                CreateUserRequest.builder()
                        .name("Professor John Smith")
                        .email("john.smith@university.edu")
                        .role("TEACHER")
                        .build()
        );

        for (CreateUserRequest request : teacherRequests) {
            UserResponse teacher = userService.createUser(request);
            log.info("Created teacher user: {}", teacher.getEmail());
        }

        // Создаем студентов
        List<CreateUserRequest> studentRequests = List.of(
                CreateUserRequest.builder()
                        .name("Alice Johnson")
                        .email("alice.johnson@student.edu")
                        .role("STUDENT")
                        .build()
        );

        // Создаем администратора
        CreateUserRequest adminRequest = CreateUserRequest.builder()
                .name("Admin User")
                .email("admin@platform.edu")
                .role("ADMIN")
                .build();
        UserResponse admin = userService.createUser(adminRequest);
        log.info("Created admin user: {}", admin.getEmail());

        for (CreateUserRequest request : studentRequests) {
            UserResponse student = userService.createUser(request);
            log.info("Created student user: {}", student.getEmail());
        }

        log.info("Demo users created: {} total users", userRepository.count());
    }

    private void createDemoCategories() {
        log.info("Creating demo categories...");

        List<CreateCategoryRequest> categoryRequests = List.of(
                CreateCategoryRequest.builder()
                        .name("Programming")
                        .description("Programming and software development courses")
                        .build(),
                CreateCategoryRequest.builder()
                        .name("Data Science")
                        .description("Data analysis, machine learning and AI")
                        .build(),
                CreateCategoryRequest.builder()
                        .name("Design")
                        .description("UI/UX and graphic design courses")
                        .build(),
                CreateCategoryRequest.builder()
                        .name("Business")
                        .description("Business, management and entrepreneurship")
                        .build(),
                CreateCategoryRequest.builder()
                        .name("Marketing")
                        .description("Digital marketing and SEO courses")
                        .build(),
                CreateCategoryRequest.builder()
                        .name("DevOps")
                        .description("Infrastructure, CI/CD and cloud technologies")
                        .build()
        );

        for (CreateCategoryRequest request : categoryRequests) {
            CategoryResponse category = categoryService.createCategory(request);
            log.info("Created category: {}", category.getName());
        }

        log.info("Demo categories created: {} total categories", categoryRepository.count());
    }

    private void createDemoCourses() {
        log.info("Creating demo courses...");

        // Получаем ID преподавателей (первые созданные пользователи с ролью TEACHER)
        List<Long> teacherIds = userRepository.findAll().stream()
                .filter(user -> "TEACHER".equals(user.getRole()))
                .map(User::getId)
                .toList();

        // Получаем ID категорий
        List<Long> categoryIds = categoryRepository.findAll().stream()
                .map(Category::getId)
                .toList();

        if (teacherIds.isEmpty() || categoryIds.isEmpty()) {
            log.error("Cannot create demo courses: no teachers or categories found");
            return;
        }

        List<CreateCourseRequest> courseRequests = List.of(
                // Программирование
                CreateCourseRequest.builder()
                        .title("Java Programming Masterclass")
                        .description("Complete Java programming course from beginner to advanced")
                        .teacherId(teacherIds.get(0))
                        .categoryId(categoryIds.get(0))
                        .startDate(LocalDate.now().plusDays(10))
                        .duration("90")
                        .build(),
                CreateCourseRequest.builder()
                        .title("Python for Beginners")
                        .description("Learn Python programming from scratch with practical projects")
                        .teacherId(teacherIds.get(0))
                        .categoryId(categoryIds.get(0))
                        .startDate(LocalDate.now().plusDays(15))
                        .duration("60")
                        .build(),
                CreateCourseRequest.builder()
                        .title("Web Development with React")
                        .description("Build modern web applications using React and Node.js")
                        .teacherId(teacherIds.get(1))
                        .categoryId(categoryIds.get(0))
                        .startDate(LocalDate.now().plusDays(20))
                        .duration("75")
                        .build(),

                // Data Science
                CreateCourseRequest.builder()
                        .title("Python for Data Science")
                        .description("Learn Python for data analysis and machine learning")
                        .teacherId(teacherIds.get(1))
                        .categoryId(categoryIds.get(1))
                        .startDate(LocalDate.now().plusDays(25))
                        .duration("60")
                        .build(),
                CreateCourseRequest.builder()
                        .title("Machine Learning Fundamentals")
                        .description("Introduction to machine learning algorithms and techniques")
                        .teacherId(teacherIds.get(2))
                        .categoryId(categoryIds.get(1))
                        .startDate(LocalDate.now().plusDays(30))
                        .duration("90")
                        .build(),

                // Дизайн
                CreateCourseRequest.builder()
                        .title("UI/UX Design Fundamentals")
                        .description("Learn user interface and user experience design principles")
                        .teacherId(teacherIds.get(2))
                        .categoryId(categoryIds.get(2))
                        .startDate(LocalDate.now().plusDays(35))
                        .duration("45")
                        .build()
        );

        for (CreateCourseRequest request : courseRequests) {
            try {
                CourseResponse course = courseService.createCourse(request);
                log.info("Created course: {}", course.getTitle());
            } catch (Exception e) {
                log.error("Failed to create course: {}", request.getTitle(), e);
            }
        }

        log.info("Demo courses created: {} total courses", courseRepository.count());
    }

    public void resetDemoData() {
        log.info("Resetting demo data...");
        clearExistingData();
        initializeDemoData();
        log.info("Demo data reset completed");
    }
}