package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.DepartmentWithEmployeesResponse;
import com.example.demo.dto.EmployeeResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.SearchRequest;
import com.example.demo.dto.SearchResponse;
import com.example.demo.service.OrganizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OrganizationControllerTest {

    @Mock
    private OrganizationService organizationService;

    @InjectMocks
    private OrganizationController organizationController;

    private DepartmentWithEmployeesResponse departmentWithEmployeesResponse;
    private SearchResponse searchResponse;
    private SearchRequest searchRequest;

    @BeforeEach
    void setUp() {
        EmployeeResponse employeeResponse = EmployeeResponse.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john.doe@example.com")
                .departmentId(UUID.randomUUID())
                .build();

        departmentWithEmployeesResponse = DepartmentWithEmployeesResponse.builder()
                .id(UUID.randomUUID())
                .name("Engineering")
                .code("ENG")
                .employees(List.of(employeeResponse))
                .build();

        PageResponse<EmployeeResponse> pageResponse = PageResponse.<EmployeeResponse>builder()
                .content(List.of(employeeResponse))
                .currentPage(0)
                .pageSize(10)
                .totalPages(1)
                .totalElements(1)
                .hasNext(false)
                .hasPrevious(false)
                .hasContent(true)
                .numberOfElements(1)
                .build();

        searchResponse = SearchResponse.builder()
                .type("employees")
                .results(pageResponse)
                .isDepartmentSearch(false)
                .searchTerm("John")
                .build();

        searchRequest = SearchRequest.builder()
                .type("employees")
                .searchTerm("John")
                .build();
    }

    @Test
    void testGetDepartmentWithEmployees_Success() {

        UUID id = UUID.randomUUID();
        when(organizationService.getDepartmentWithEmployees(id)).thenReturn(departmentWithEmployeesResponse);


        ResponseEntity<ApiResponse<DepartmentWithEmployeesResponse>> response =
                organizationController.getDepartmentWithEmployees(id);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Department with employees retrieved successfully", response.getBody().message());
        assertEquals(departmentWithEmployeesResponse, response.getBody().data());
        assertNotNull(response.getBody().traceId());
        verify(organizationService, times(1)).getDepartmentWithEmployees(id);
    }

    @Test
    void testSearchWithPagination_Success() {

        when(organizationService.searchWithPagination(any(SearchRequest.class))).thenReturn(searchResponse);


        ResponseEntity<ApiResponse<SearchResponse>> response =
                organizationController.searchWithPagination(searchRequest, 0, 10, "name", "ASC");


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Search completed successfully", response.getBody().message());
        assertEquals(searchResponse, response.getBody().data());
        assertNotNull(response.getBody().traceId());
        verify(organizationService, times(1)).searchWithPagination(any(SearchRequest.class));
    }

    @Test
    void testSearchWithPagination_WithDefaults() {

        when(organizationService.searchWithPagination(any(SearchRequest.class))).thenReturn(searchResponse);


        ResponseEntity<ApiResponse<SearchResponse>> response =
                organizationController.searchWithPagination(searchRequest, 0, 10, "name", "ASC");


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Search completed successfully", response.getBody().message());
        assertEquals(searchResponse, response.getBody().data());
        assertNotNull(searchRequest.getPageRequest());
        assertEquals(0, searchRequest.getPageRequest().getPage());
        assertEquals(10, searchRequest.getPageRequest().getSize());
        assertEquals("name", searchRequest.getPageRequest().getSortBy());
        assertEquals("ASC", searchRequest.getPageRequest().getDirection());
        verify(organizationService, times(1)).searchWithPagination(any(SearchRequest.class));
    }
}
