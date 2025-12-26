package ru.skillfactory.learning.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillfactory.learning.platform.entity.AnswerOption;

import java.util.List;

@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {

    List<AnswerOption> findByQuestionId(Long questionId);

    @Query("SELECT ao FROM AnswerOption ao WHERE ao.question.id = :questionId AND ao.isCorrect = true")
    List<AnswerOption> findCorrectAnswersByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT COUNT(ao) FROM AnswerOption ao WHERE ao.question.id = :questionId AND ao.isCorrect = true")
    Long countCorrectAnswersByQuestionId(@Param("questionId") Long questionId);

    boolean existsByQuestionIdAndIsCorrect(Long questionId, Boolean isCorrect);

    @Query("SELECT ao FROM AnswerOption ao WHERE ao.question.quiz.id = :quizId")
    List<AnswerOption> findByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT ao FROM AnswerOption ao WHERE ao.question.quiz.id = :quizId AND ao.isCorrect = true")
    List<AnswerOption> findCorrectAnswersByQuizId(@Param("quizId") Long quizId);

    void deleteByQuestionId(Long questionId);
}
