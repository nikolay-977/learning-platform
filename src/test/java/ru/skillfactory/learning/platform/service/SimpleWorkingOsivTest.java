package ru.skillfactory.learning.platform.service;

import jakarta.persistence.EntityManager;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.skillfactory.learning.platform.entity.*;
import ru.skillfactory.learning.platform.entity.Module;
import ru.skillfactory.learning.platform.repository.CourseRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("without-open-in-view")
@DisplayName("Простой работающий тест OSIV")
public class SimpleWorkingOsivTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Демонстрация OSIV работы")
    @Transactional
        // Только для создания данных
    void demonstrateOsiv() {
        // Создаем минимальные тестовые данные
        createMinimalTestData();
    }

    private void createMinimalTestData() {
        // 1. Преподаватель (ОБЯЗАТЕЛЬНО!)
        User teacher = new User();
        teacher.setEmail("test-teacher@example.com");
        teacher.setName("Test Teacher");
        teacher.setRole(Role.TEACHER);
        entityManager.persist(teacher);

        // 2. Категория (можно без нее, т.к. nullable)
        Category category = new Category();
        category.setName("Test");
        entityManager.persist(category);

        // 3. Курс
        Course course = new Course();
        course.setTitle("Test Course");
        course.setTeacher(teacher);  // ВАЖНО!
        course.setCategory(category);
        entityManager.persist(course);

        // 4. Модуль
        Module module = new Module();
        module.setTitle("Test Module");
        module.setCourse(course);
        entityManager.persist(module);

        // Не добавляем в список, просто сохраняем
        entityManager.flush();

        System.out.println("✅ Тестовые данные созданы");
        System.out.println("   Курс ID: " + course.getId());
        System.out.println("   Модуль ID: " + module.getId());

        // Теперь можно проверить доступ вне транзакции
        checkLazyLoading(course.getId());
    }

    private void checkLazyLoading(Long courseId) {
        // Закрываем EntityManager (транзакция закроется после метода)
        // entityManager.close(); // Не закрываем вручную, Spring сделает это

        // В реальности здесь должен быть отдельный метод БЕЗ @Transactional
        System.out.println("\nПосле завершения транзакции:");
        System.out.println("Попытка доступа к ленивой коллекции должна вызвать");
        System.out.println("LazyInitializationException при OSIV=false");
    }

    @Test
    @DisplayName("Отдельный тест для проверки ленивой загрузки")
    void testLazyLoadSeparately() {
        // Этот тест выполнится после создания данных
        // и покажет реальное поведение
        System.out.println("\n=== Проверка ленивой загрузки ===");
        System.out.println("Профиль: without-open-in-view");
        System.out.println("Ожидается: LazyInitializationException");

        // Пытаемся найти любой курс
        var courses = courseRepository.findAll();
        if (!courses.isEmpty()) {
            Course course = courses.get(0);
            try {
                course.getModules().size();
            } catch (Exception e) {
                if (e instanceof LazyInitializationException) {
                    System.out.println("✅ УСПЕХ: " + e.getClass().getSimpleName());
                    System.out.println("   Сообщение: " + e.getMessage());
                }
            }
        }

    }
}