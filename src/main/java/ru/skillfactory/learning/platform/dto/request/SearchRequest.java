package ru.skillfactory.learning.platform.dto.request;

import lombok.Data;

@Data
public class SearchRequest {
    private String keyword;
    private Long categoryId;
    private Long teacherId;
    private String sortBy = "title";
    private String sortDirection = "asc";
    private Integer page = 0;
    private Integer size = 20;
}
