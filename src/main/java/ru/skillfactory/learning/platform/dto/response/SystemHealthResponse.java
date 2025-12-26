package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;

@Data
public class SystemHealthResponse {
    private String status;
    private Long totalUsers;
    private Long activeUsers;
    private Long totalCourses;
    private Long activeCourses;
    private Long totalEnrollments;
    private Long activeEnrollments;
    private DatabaseInfo database;
    private MemoryInfo memory;
    private Long uptime; // в секундах

    @Data
    public static class DatabaseInfo {
        private String status;
        private Long connectionCount;
        private Long queryCount;
    }

    @Data
    public static class MemoryInfo {
        private Long totalMemory;
        private Long usedMemory;
        private Long freeMemory;
        private Double usagePercentage;
    }
}
