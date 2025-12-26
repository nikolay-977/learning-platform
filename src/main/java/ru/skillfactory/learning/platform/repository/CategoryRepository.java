package ru.skillfactory.learning.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillfactory.learning.platform.entity.Category;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    List<Category> findByNameContainingIgnoreCase(String name);

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.courses WHERE c.id = :id")
    Optional<Category> findByIdWithCourses(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.courses")
    List<Category> findAllWithCourses();

    @Query("SELECT c FROM Category c LEFT JOIN c.courses GROUP BY c.id ORDER BY COUNT(c.courses) DESC")
    List<Category> findAllWithCourseCount();

    @Query("SELECT c FROM Category c WHERE SIZE(c.courses) > 0")
    List<Category> findCategoriesWithCourses();
}
