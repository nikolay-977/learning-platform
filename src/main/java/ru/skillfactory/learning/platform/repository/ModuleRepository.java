package ru.skillfactory.learning.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillfactory.learning.platform.entity.Module;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByCourseId(Long courseId);

    @Query("SELECT m FROM Module m LEFT JOIN FETCH m.lessons WHERE m.id = :id")
    Optional<Module> findByIdWithLessons(@Param("id") Long id);

    @Query("SELECT m FROM Module m LEFT JOIN FETCH m.lessons WHERE m.course.id = :courseId ORDER BY m.orderIndex")
    List<Module> findByCourseIdWithLessons(@Param("courseId") Long courseId);
}
