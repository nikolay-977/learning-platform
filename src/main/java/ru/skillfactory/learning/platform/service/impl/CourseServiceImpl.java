package ru.skillfactory.learning.platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillfactory.learning.platform.dto.request.CreateCourseRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateCourseRequest;
import ru.skillfactory.learning.platform.dto.response.CourseDetailResponse;
import ru.skillfactory.learning.platform.dto.response.CourseResponse;
import ru.skillfactory.learning.platform.entity.*;
import ru.skillfactory.learning.platform.entity.Module;
import ru.skillfactory.learning.platform.exception.ResourceNotFoundException;
import ru.skillfactory.learning.platform.mapper.CourseMapper;
import ru.skillfactory.learning.platform.repository.*;
import ru.skillfactory.learning.platform.service.CourseService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TagRepository tagRepository;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        log.info("Creating course: {}", request.getTitle());

        // Проверяем преподавателя
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getTeacherId()));

        if (teacher.getRole() != Role.TEACHER) {
            throw new IllegalArgumentException("User is not a teacher");
        }

        // Проверяем категорию
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        // Создаем курс
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setDuration(request.getDuration());
        course.setStartDate(request.getStartDate());
        course.setTeacher(teacher);
        course.setCategory(category);

        Course savedCourse = courseRepository.save(course);
        log.info("Course created with ID: {}", savedCourse.getId());

        return courseMapper.toResponse(savedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        log.info("Getting course by ID: {}", id);

        Course course = courseRepository.findByIdWithModulesAndTeacher(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseDetailById(Long id) {
        log.info("Getting course detail by ID: {}", id);

        Course course = courseRepository.findByIdWithModulesAndTeacher(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        // Явно загружаем модули с уроками
        if (course.getModules() != null) {
            course.getModules().forEach(module -> {
                if (module.getLessons() != null) {
                    module.getLessons().size(); // Инициализация ленивой коллекции
                }
            });
        }

        return toDetailResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        log.info("Getting all courses");

        List<Course> courses = courseRepository.findAll();

        return courses.stream()
                .map(courseMapper::toResponse) // Теперь работает с экземплярным методом
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByCategory(Long categoryId) {
        log.info("Getting courses by category ID: {}", categoryId);

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }

        List<Course> courses = courseRepository.findByCategoryId(categoryId);

        return courses.stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByTeacher(Long teacherId) {
        log.info("Getting courses by teacher ID: {}", teacherId);

        if (!userRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("User", "id", teacherId);
        }

        List<Course> courses = courseRepository.findByTeacherId(teacherId);

        return courses.stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> searchCourses(String keyword) {
        log.info("Searching courses with keyword: {}", keyword);

        List<Course> courses = courseRepository.searchByKeyword(keyword);

        return courses.stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long id, UpdateCourseRequest request) {
        log.info("Updating course with ID: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        if (request.getTitle() != null) {
            course.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getDuration() != null) {
            course.setDuration(request.getDuration());
        }
        if (request.getStartDate() != null) {
            course.setStartDate(request.getStartDate());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            course.setCategory(category);
        }

        Course updatedCourse = courseRepository.save(course);
        log.info("Course updated with ID: {}", updatedCourse.getId());

        return courseMapper.toResponse(updatedCourse);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        log.info("Deleting course with ID: {}", id);

        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course", "id", id);
        }

        courseRepository.deleteById(id);
        log.info("Course deleted with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserEnrolled(Long courseId, Long userId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(userId, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getEnrolledCourses(Long userId) {
        log.info("Getting enrolled courses for user ID: {}", userId);

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(userId);

        return enrollments.stream()
                .map(Enrollment::getCourse)
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public int getEnrolledStudentsCount(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).size();
    }

    @Override
    @Transactional
    public CourseResponse addTagToCourse(Long courseId, Long tagId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));

        course.getTags().add(tag);
        Course updatedCourse = courseRepository.save(course);

        return courseMapper.toResponse(updatedCourse);
    }

    @Override
    @Transactional
    public CourseResponse removeTagFromCourse(Long courseId, Long tagId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));

        course.getTags().remove(tag);
        Course updatedCourse = courseRepository.save(course);

        return courseMapper.toResponse(updatedCourse);
    }

    private CourseDetailResponse toDetailResponse(Course course) {
        CourseDetailResponse response = new CourseDetailResponse();
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        response.setDescription(course.getDescription());
        response.setDuration(course.getDuration());
        response.setStartDate(course.getStartDate());

        if (course.getCategory() != null) {
            response.setCategoryId(course.getCategory().getId());
            response.setCategoryName(course.getCategory().getName());
        }

        if (course.getTeacher() != null) {
            response.setTeacherId(course.getTeacher().getId());
            response.setTeacherName(course.getTeacher().getName());
        }

        // Модули с уроками
        if (course.getModules() != null) {
            response.setModules(course.getModules().stream()
                    .map(this::moduleToResponse)
                    .collect(Collectors.toList()));
        }

        // Теги
        if (course.getTags() != null) {
            response.setTags(course.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList()));
        }

        // Отзывы
        if (course.getReviews() != null) {
            response.setReviews(course.getReviews().stream()
                    .map(review -> {
                        CourseDetailResponse.ReviewResponse reviewResponse = new CourseDetailResponse.ReviewResponse();
                        reviewResponse.setId(review.getId());
                        reviewResponse.setRating(review.getRating());
                        reviewResponse.setComment(review.getComment());
                        reviewResponse.setCreatedAt(review.getCreatedAt());
                        if (review.getStudent() != null) {
                            reviewResponse.setStudentName(review.getStudent().getName());
                        }
                        return reviewResponse;
                    })
                    .collect(Collectors.toList()));

            // Средний рейтинг
            double average = course.getReviews().stream()
                    .mapToInt(CourseReview::getRating)
                    .average()
                    .orElse(0.0);
            response.setAverageRating(Math.round(average * 10.0) / 10.0);
        }

        return response;
    }

    private CourseDetailResponse.ModuleResponse moduleToResponse(Module module) {
        CourseDetailResponse.ModuleResponse response = new CourseDetailResponse.ModuleResponse();
        response.setId(module.getId());
        response.setTitle(module.getTitle());
        response.setDescription(module.getDescription());
        response.setOrderIndex(module.getOrderIndex());

        // Уроки
        if (module.getLessons() != null) {
            response.setLessons(module.getLessons().stream()
                    .map(lesson -> {
                        CourseDetailResponse.LessonResponse lessonResponse = new CourseDetailResponse.LessonResponse();
                        lessonResponse.setId(lesson.getId());
                        lessonResponse.setTitle(lesson.getTitle());
                        lessonResponse.setContent(lesson.getContent());
                        lessonResponse.setVideoUrl(lesson.getVideoUrl());
                        return lessonResponse;
                    })
                    .collect(Collectors.toList()));
        }

        return response;
    }
}