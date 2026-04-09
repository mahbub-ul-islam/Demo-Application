package com.example.demo.service.impl;

import com.example.demo.domain.Department;
import com.example.demo.domain.Employee;
import com.example.demo.dto.DepartmentResponse;
import com.example.demo.dto.DepartmentWithEmployeesResponse;
import com.example.demo.dto.EmployeeResponse;
import com.example.demo.dto.PageRequest;
import com.example.demo.dto.SearchRequest;
import com.example.demo.dto.SearchResponse;
import com.example.demo.exception.errors.data.DataNotFoundException;
import com.example.demo.mapper.DepartmentMapper;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OrganizationServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    private Department department;
    private Employee employee;
    private DepartmentResponse departmentResponse;
    private EmployeeResponse employeeResponse;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .name("Engineering")
                .code("ENG")
                .build();

        employee = Employee.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .salary(BigDecimal.valueOf(50000.0))
                .department(department)
                .build();

        department.setEmployees(List.of(employee));

        departmentResponse = DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .build();

        employeeResponse = EmployeeResponse.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .salary(employee.getSalary())
                .departmentId(department.getId())
                .build();
    }

    @Test
    void testGetDepartmentWithEmployees_Success() {

        UUID departmentId = department.getId();
        when(departmentRepository.findDepartmentWithEmployees(departmentId)).thenReturn(department);
        when(employeeMapper.toResponseList(any())).thenReturn(List.of(employeeResponse));

        DepartmentWithEmployeesResponse result = organizationService.getDepartmentWithEmployees(departmentId);


        assertNotNull(result);
        assertEquals(department.getId(), result.getId());
        assertEquals(department.getName(), result.getName());
        assertEquals(department.getCode(), result.getCode());
        assertEquals(1, result.getEmployees().size());
        verify(departmentRepository, times(1)).findDepartmentWithEmployees(departmentId);
        verify(employeeMapper, times(1)).toResponseList(any());
    }

    @Test
    void testGetDepartmentWithEmployees_NotFound_ThrowsException() {

        UUID departmentId = UUID.randomUUID();
        when(departmentRepository.findDepartmentWithEmployees(departmentId)).thenReturn(null);


        DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> organizationService.getDepartmentWithEmployees(departmentId)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(departmentRepository, times(1)).findDepartmentWithEmployees(departmentId);
        verify(employeeMapper, never()).toResponseList(any());
    }

    @Test
    void testSearchWithPagination_Departments_Success() {

        SearchRequest searchRequest = SearchRequest.builder()
                .type("departments")
                .searchTerm("Engineering")
                .pageRequest(PageRequest.builder()
                        .page(0)
                        .size(10)
                        .sortBy("name")
                        .direction("ASC")
                        .build())
                .build();

        Page<Department> departmentPage = new PageImpl<>(List.of(department));
        when(departmentRepository.findByNameContainingIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(departmentPage);
        when(departmentMapper.toResponse(department)).thenReturn(departmentResponse);


        SearchResponse result = organizationService.searchWithPagination(searchRequest);


        assertNotNull(result);
        assertEquals("departments", result.getType());
        assertTrue(result.isDepartmentSearch());
        assertEquals("Engineering", result.getSearchTerm());
        assertNotNull(result.getResults());
        verify(departmentRepository, times(1)).findByNameContainingIgnoreCase(anyString(), any(Pageable.class));
        verify(departmentMapper, times(1)).toResponse(department);
    }

    @Test
    void testSearchWithPagination_Departments_NoSearchTerm() {

        SearchRequest searchRequest = SearchRequest.builder()
                .type("departments")
                .searchTerm(null)
                .pageRequest(PageRequest.builder()
                        .page(0)
                        .size(10)
                        .sortBy("name")
                        .direction("ASC")
                        .build())
                .build();

        Page<Department> departmentPage = new PageImpl<>(List.of(department));
        when(departmentRepository.findAll(any(Pageable.class))).thenReturn(departmentPage);
        when(departmentMapper.toResponse(department)).thenReturn(departmentResponse);


        SearchResponse result = organizationService.searchWithPagination(searchRequest);


        assertNotNull(result);
        assertEquals("departments", result.getType());
        verify(departmentRepository, times(1)).findAll(any(Pageable.class));
        verify(departmentMapper, times(1)).toResponse(department);
    }

    @Test
    void testSearchWithPagination_Employees_Success() {

        SearchRequest searchRequest = SearchRequest.builder()
                .type("employees")
                .searchTerm("John")
                .pageRequest(PageRequest.builder()
                        .page(0)
                        .size(10)
                        .sortBy("name")
                        .direction("ASC")
                        .build())
                .build();

        Page<Employee> employeePage = new PageImpl<>(List.of(employee));
        when(employeeRepository.findByNameContainingIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(employeePage);
        when(employeeMapper.toResponseList(any())).thenReturn(List.of(employeeResponse));


        SearchResponse result = organizationService.searchWithPagination(searchRequest);


        assertNotNull(result);
        assertEquals("employees", result.getType());
        assertFalse(result.isDepartmentSearch());
        assertEquals("John", result.getSearchTerm());
        assertNotNull(result.getResults());
        verify(employeeRepository, times(1)).findByNameContainingIgnoreCase(anyString(), any(Pageable.class));
        verify(employeeMapper, times(1)).toResponseList(any());
    }

    @Test
    void testSearchWithPagination_Employees_WithDepartmentId() {

        SearchRequest searchRequest = SearchRequest.builder()
                .type("employees")
                .searchTerm("John")
                .departmentId(department.getId())
                .pageRequest(PageRequest.builder()
                        .page(0)
                        .size(10)
                        .sortBy("name")
                        .direction("ASC")
                        .build())
                .build();

        Page<Employee> employeePage = new PageImpl<>(List.of(employee));
        when(employeeRepository.findByNameContainingIgnoreCaseAndDepartmentId(anyString(), any(UUID.class), any(Pageable.class)))
                .thenReturn(employeePage);
        when(employeeMapper.toResponseList(any())).thenReturn(List.of(employeeResponse));


        SearchResponse result = organizationService.searchWithPagination(searchRequest);


        assertNotNull(result);
        assertEquals("employees", result.getType());
        verify(employeeRepository, times(1)).findByNameContainingIgnoreCaseAndDepartmentId(
                anyString(), any(UUID.class), any(Pageable.class));
    }

    @Test
    void testSearchWithPagination_Employees_WithDepartmentCode() {

        SearchRequest searchRequest = SearchRequest.builder()
                .type("employees")
                .searchTerm("John")
                .departmentCode("ENG")
                .pageRequest(PageRequest.builder()
                        .page(0)
                        .size(10)
                        .sortBy("name")
                        .direction("ASC")
                        .build())
                .build();

        Page<Employee> employeePage = new PageImpl<>(List.of(employee));
        when(departmentRepository.findByCode("ENG")).thenReturn(department);
        when(employeeRepository.findByNameContainingIgnoreCaseAndDepartmentId(anyString(), any(UUID.class), any(Pageable.class)))
                .thenReturn(employeePage);
        when(employeeMapper.toResponseList(any())).thenReturn(List.of(employeeResponse));


        SearchResponse result = organizationService.searchWithPagination(searchRequest);


        assertNotNull(result);
        assertEquals("employees", result.getType());
        verify(departmentRepository, times(1)).findByCode("ENG");
        verify(employeeRepository, times(1)).findByNameContainingIgnoreCaseAndDepartmentId(
                anyString(), any(UUID.class), any(Pageable.class));
    }

    @Test
    void testSearchWithPagination_Employees_NoSearchTerm() {

        SearchRequest searchRequest = SearchRequest.builder()
                .type("employees")
                .searchTerm(null)
                .pageRequest(PageRequest.builder()
                        .page(0)
                        .size(10)
                        .sortBy("name")
                        .direction("ASC")
                        .build())
                .build();

        Page<Employee> employeePage = new PageImpl<>(List.of(employee));
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(employeePage);
        when(employeeMapper.toResponseList(any())).thenReturn(List.of(employeeResponse));


        SearchResponse result = organizationService.searchWithPagination(searchRequest);


        assertNotNull(result);
        assertEquals("employees", result.getType());
        verify(employeeRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testSearchWithPagination_InvalidType_ThrowsException() {

        SearchRequest searchRequest = SearchRequest.builder()
                .type("invalid")
                .build();


        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> organizationService.searchWithPagination(searchRequest)
        );

        assertTrue(exception.getMessage().contains("Invalid search type"));
    }
}
