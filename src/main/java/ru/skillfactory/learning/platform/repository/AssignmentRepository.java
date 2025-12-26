package ru.skillfactory.learning.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillfactory.learning.platform.entity.Assignment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByLessonId(Long lessonId);

    @Query("SELECT a FROM Assignment a LEFT JOIN FETCH a.submissions WHERE a.id = :id")
    Optional<Assignment> findByIdWithSubmissions(@Param("id") Long id);

    @Query("SELECT a FROM Assignment a WHERE a.dueDate < :date AND a.lesson.module.course.teacher.id = :teacherId")
    List<Assignment> findOverdueAssignmentsForTeacher(@Param("date") LocalDate date, @Param("teacherId") Long teacherId);
}
