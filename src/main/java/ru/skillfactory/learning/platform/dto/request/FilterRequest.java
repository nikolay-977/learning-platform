package ru.skillfactory.learning.platform.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class FilterRequest {
    private List<Long> categoryIds;
    private List<Long> tagIds;
    private Integer minRating;
    private Boolean isActive = true;
    private Boolean hasEnrollments;
}
