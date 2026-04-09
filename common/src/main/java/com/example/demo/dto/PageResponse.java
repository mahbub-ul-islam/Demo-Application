package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {


    private List<T> content;

    private int currentPage;

    private int pageSize;

    private int totalPages;

    private long totalElements;

    private boolean hasNext;

    private boolean hasPrevious;

    private boolean hasContent;

    private int numberOfElements;
}
