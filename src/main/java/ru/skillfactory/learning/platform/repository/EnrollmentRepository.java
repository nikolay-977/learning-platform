package ru.skillfactory.learning.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillfactory.learning.platform.entity.Enrollment;
import ru.skillfactory.learning.platform.entity.EnrollmentStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByCourseId(Long courseId);
    List<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);

    @Query("SELECT e FROM Enrollment e LEFT JOIN FETCH e.course LEFT JOIN FETCH e.student WHERE e.id = :id")
    Optional<Enrollment> findByIdWithDetails(@Param("id") Long id);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
}
