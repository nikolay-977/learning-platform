package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class DashboardResponse {

    // Для преподавателя
    @Data
    public static class TeacherDashboard {
        private Long coursesTaught;
        private Long totalStudents;
        private Long assignmentsCreated;
        private Long submissionsToGrade;
        private Double averageCourseRating;
        private List<CourseResponse> recentCourses;
        private List<SubmissionResponse> recentSubmissions;
        private List<QuizResultResponse> recentQuizResults;
    }

    // Для студента
    @Data
    public static class StudentDashboard {
        private Long enrolledCourses;
        private Long assignmentsDue;
        private Long submissionsMade;
        private Long quizzesTaken;
        private Double averageScore;
        private List<CourseResponse> activeCourses;
        private List<AssignmentResponse> upcomingAssignments;
        private List<SubmissionResponse> recentSubmissions;
        private List<QuizResultResponse> recentQuizResults;
    }

    // Для администратора
    @Data
    public static class AdminDashboard {
        private StatisticsResponse statistics;
        private List<UserResponse> recentUsers;
        private List<CourseResponse> recentCourses;
        private List<EnrollmentResponse> recentEnrollments;
        private SystemHealthResponse systemHealth;
    }
}
