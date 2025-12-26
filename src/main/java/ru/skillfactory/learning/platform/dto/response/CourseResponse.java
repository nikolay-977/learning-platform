package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private String duration;
    private LocalDate startDate;
    private Long categoryId;
    private String categoryName;
    private Long teacherId;
    private String teacherName;
    private Integer moduleCount;
    private Integer enrolledStudents;
    private Double averageRating;
}