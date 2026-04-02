package com.friends.executionservice.dto.common;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedResponse<T> {
    private List<T> content;
    private long totalElements;
    private int page;
    private int size;
    private int totalPages;
}

