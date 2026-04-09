package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.DepartmentWithEmployeesResponse;
import com.example.demo.dto.PageRequest;
import com.example.demo.dto.SearchRequest;
import com.example.demo.dto.SearchResponse;
import com.example.demo.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping("/departments-with-employees")
    public ResponseEntity<ApiResponse<DepartmentWithEmployeesResponse>> getDepartmentWithEmployees(@RequestParam UUID id) {
        DepartmentWithEmployeesResponse response = organizationService.getDepartmentWithEmployees(id);
        String traceId = getTraceId();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of(
                response,
                "Department with employees retrieved successfully",
                "/api/v1/organization/departments-with-employees?id=" + id,
                traceId,
                HttpStatus.OK));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<SearchResponse>> searchWithPagination(
            @Valid @ModelAttribute SearchRequest searchRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        
        String traceId = getTraceId();

        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .build();

        searchRequest.setPageRequest(pageRequest);
        
        SearchResponse response = organizationService.searchWithPagination(searchRequest);

        String url = "/api/v1/organization/search?type=" + searchRequest.getType() + 
                     "&searchTerm=" + (searchRequest.getSearchTerm() != null ? searchRequest.getSearchTerm() : "") +
                     "&page=" + page + "&size=" + size + "&sortBy=" + sortBy + "&direction=" + direction;

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of(
                response,
                "Search completed successfully",
                url,
                traceId,
                HttpStatus.OK));
    }

    private String getTraceId() {
        String traceId = MDC.get("traceId");
        return (traceId != null) ? traceId : UUID.randomUUID().toString();
    }
}
