package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    private String type;

    private PageResponse<?> results;

    private boolean isDepartmentSearch;

    private String searchTerm;
}
