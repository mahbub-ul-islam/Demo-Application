package com.example.demo.service.impl;

import com.example.demo.domain.Department;
import com.example.demo.domain.Employee;
import com.example.demo.dto.DepartmentWithEmployeesResponse;
import com.example.demo.dto.EmployeeResponse;
import com.example.demo.dto.DepartmentResponse;
import com.example.demo.dto.PageRequest;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.SearchRequest;
import com.example.demo.dto.SearchResponse;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.mapper.DepartmentMapper;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.exception.errors.data.DataNotFoundException;
import com.example.demo.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.MDC;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final DepartmentMapper departmentMapper;

    @Transactional(readOnly = true)
    public DepartmentWithEmployeesResponse getDepartmentWithEmployees(UUID id) {
        String traceId = MDC.get("traceId");

        Department department = departmentRepository.findDepartmentWithEmployees(id);
        
        if (department == null) {
            throw new DataNotFoundException(
                "Department with ID " + id + " not found",
                traceId,
                "Cannot search for a department that does not exist"
            );
        }
        
        List<EmployeeResponse> employeeResponses = employeeMapper.toResponseList(department.getEmployees());
        
        return DepartmentWithEmployeesResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .employees(employeeResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public SearchResponse searchWithPagination(SearchRequest searchRequest) {
        String traceId = MDC.get("traceId");
        
        if ("departments".equals(searchRequest.getType())) {
            return searchDepartments(searchRequest, traceId);
        } else if ("employees".equals(searchRequest.getType())) {
            return searchEmployees(searchRequest, traceId);
        } else {
            throw new IllegalArgumentException("Invalid search type: " + searchRequest.getType());
        }
    }

    private SearchResponse searchDepartments(SearchRequest searchRequest, String traceId) {
        PageRequest pageRequest = searchRequest.getPageRequest();

        Sort.Direction direction = (pageRequest != null && "DESC".equalsIgnoreCase(pageRequest.getDirection())) 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        int page = (pageRequest != null) ? pageRequest.getPage() : 0;
        int size = (pageRequest != null) ? pageRequest.getSize() : 10;
        String sortBy = (pageRequest != null && pageRequest.getSortBy() != null) ? pageRequest.getSortBy() : "name";
        
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
            page, 
            size, 
            Sort.by(direction, sortBy)
        );

        Page<Department> departmentPage;
        if (searchRequest.getSearchTerm() != null && !searchRequest.getSearchTerm().trim().isEmpty()) {
            departmentPage = departmentRepository.findByNameContainingIgnoreCase(
                searchRequest.getSearchTerm(), pageable);
        } else {
            departmentPage = departmentRepository.findAll(pageable);
        }

        List<DepartmentResponse> departmentResponses = departmentPage.getContent().stream()
            .map(departmentMapper::toResponse)
            .toList();

        PageResponse<DepartmentResponse> pageResponse = PageResponse.<DepartmentResponse>builder()
            .content(departmentResponses)
            .currentPage(departmentPage.getNumber())
            .pageSize(departmentPage.getSize())
            .totalPages(departmentPage.getTotalPages())
            .totalElements(departmentPage.getTotalElements())
            .hasNext(departmentPage.hasNext())
            .hasPrevious(departmentPage.hasPrevious())
            .hasContent(departmentPage.hasContent())
            .numberOfElements(departmentPage.getNumberOfElements())
            .build();

        return SearchResponse.builder()
            .type("departments")
            .results(pageResponse)
            .isDepartmentSearch(true)
            .searchTerm(searchRequest.getSearchTerm())
            .build();
    }

    private SearchResponse searchEmployees(SearchRequest searchRequest, String traceId) {
        PageRequest pageRequest = searchRequest.getPageRequest();

        Sort.Direction direction = (pageRequest != null && "DESC".equalsIgnoreCase(pageRequest.getDirection())) 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        int page = (pageRequest != null) ? pageRequest.getPage() : 0;
        int size = (pageRequest != null) ? pageRequest.getSize() : 10;
        String sortBy = (pageRequest != null && pageRequest.getSortBy() != null) ? pageRequest.getSortBy() : "name";
        
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
            page, 
            size, 
            Sort.by(direction, sortBy)
        );

        Page<Employee> employeePage;
        UUID departmentId = searchRequest.getDepartmentId();

        if (departmentId == null && searchRequest.getDepartmentCode() != null) {
            Department department = departmentRepository.findByCode(searchRequest.getDepartmentCode());
            if (department != null) {
                departmentId = department.getId();
            }
        }
        
        if (departmentId != null) {
            if (searchRequest.getSearchTerm() != null && !searchRequest.getSearchTerm().trim().isEmpty()) {
                employeePage = employeeRepository.findByNameContainingIgnoreCaseAndDepartmentId(
                    searchRequest.getSearchTerm(), departmentId, pageable);
            } else {
                employeePage = employeeRepository.findByDepartmentId(departmentId, pageable);
            }
        } else {
            if (searchRequest.getSearchTerm() != null && !searchRequest.getSearchTerm().trim().isEmpty()) {
                employeePage = employeeRepository.findByNameContainingIgnoreCase(
                    searchRequest.getSearchTerm(), pageable);
            } else {
                employeePage = employeeRepository.findAll(pageable);
            }
        }

        List<EmployeeResponse> employeeResponses = employeeMapper.toResponseList(employeePage.getContent());

        PageResponse<EmployeeResponse> pageResponse = PageResponse.<EmployeeResponse>builder()
            .content(employeeResponses)
            .currentPage(employeePage.getNumber())
            .pageSize(employeePage.getSize())
            .totalPages(employeePage.getTotalPages())
            .totalElements(employeePage.getTotalElements())
            .hasNext(employeePage.hasNext())
            .hasPrevious(employeePage.hasPrevious())
            .hasContent(employeePage.hasContent())
            .numberOfElements(employeePage.getNumberOfElements())
            .build();

        return SearchResponse.builder()
            .type("employees")
            .results(pageResponse)
            .isDepartmentSearch(false)
            .searchTerm(searchRequest.getSearchTerm())
            .build();
    }
}
