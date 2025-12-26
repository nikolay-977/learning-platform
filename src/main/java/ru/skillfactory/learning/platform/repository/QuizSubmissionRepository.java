package ru.skillfactory.learning.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillfactory.learning.platform.entity.QuizSubmission;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    Optional<QuizSubmission> findByQuizIdAndStudentId(Long quizId, Long studentId);
    List<QuizSubmission> findByQuizId(Long quizId);
    List<QuizSubmission> findByStudentId(Long studentId);

    @Query("SELECT qs FROM QuizSubmission qs WHERE qs.quiz.module.course.id = :courseId")
    List<QuizSubmission> findByCourseId(@Param("courseId") Long courseId);
}
