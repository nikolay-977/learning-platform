package ru.skillfactory.learning.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetailResponse {
    private Long id;
    private String name;
    private String description;
    private List<CourseResponse> courses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
