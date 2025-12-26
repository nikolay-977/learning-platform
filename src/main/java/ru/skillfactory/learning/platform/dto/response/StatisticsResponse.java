package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;
import java.util.Map;

@Data
public class StatisticsResponse {
    private Long totalCourses;
    private Long totalStudents;
    private Long totalTeachers;
    private Long totalEnrollments;
    private Long totalAssignments;
    private Long totalSubmissions;
    private Long totalQuizzes;

    // Статистика по курсам
    private Map<String, Long> coursesByCategory;
    private Double averageCourseRating;
    private Long mostPopularCourseId;
    private String mostPopularCourseTitle;

    // Статистика по активности
    private Long activeStudentsCount;
    private Long completedCoursesCount;
    private Double averageCompletionRate;

    // Статистика по заданиям
    private Double averageAssignmentScore;
    private Long overdueAssignmentsCount;
    private Long ungradedSubmissionsCount;

    // Статистика по тестам
    private Double averageQuizScore;
    private Long totalQuizAttempts;
}
