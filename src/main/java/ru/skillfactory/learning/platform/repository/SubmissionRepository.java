package ru.skillfactory.learning.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillfactory.learning.platform.entity.Submission;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
    List<Submission> findByAssignmentId(Long assignmentId);
    List<Submission> findByStudentId(Long studentId);

    @Query("SELECT s FROM Submission s LEFT JOIN FETCH s.assignment WHERE s.id = :id")
    Optional<Submission> findByIdWithAssignment(@Param("id") Long id);

    @Query("SELECT s FROM Submission s WHERE s.assignment.lesson.module.course.id = :courseId")
    List<Submission> findByCourseId(@Param("courseId") Long courseId);
}
