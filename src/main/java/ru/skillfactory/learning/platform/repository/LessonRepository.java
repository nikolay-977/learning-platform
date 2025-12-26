package ru.skillfactory.learning.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillfactory.learning.platform.entity.Lesson;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByModuleId(Long moduleId);

    @Query("SELECT l FROM Lesson l LEFT JOIN FETCH l.assignments WHERE l.id = :id")
    Optional<Lesson> findByIdWithAssignments(@Param("id") Long id);
}
