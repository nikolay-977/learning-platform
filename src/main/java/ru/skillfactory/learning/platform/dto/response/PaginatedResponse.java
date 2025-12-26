package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class PaginatedResponse<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private boolean first;
    private boolean last;

    public static <T> PaginatedResponse<T> of(List<T> content, int currentPage, int pageSize, long totalElements) {
        PaginatedResponse<T> response = new PaginatedResponse<>();
        response.setContent(content);
        response.setCurrentPage(currentPage);
        response.setPageSize(pageSize);
        response.setTotalElements(totalElements);
        response.setTotalPages((int) Math.ceil((double) totalElements / pageSize));
        response.setFirst(currentPage == 0);
        response.setLast(currentPage >= response.getTotalPages() - 1);
        return response;
    }
}
