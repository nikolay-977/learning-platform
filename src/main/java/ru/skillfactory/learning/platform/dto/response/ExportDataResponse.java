package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExportDataResponse {
    private String exportId;
    private String exportType; // COURSES, USERS, ENROLLMENTS, etc.
    private String status; // PROCESSING, COMPLETED, FAILED
    private String fileUrl;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private Integer recordsExported;
}
