package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.EmployeeRequest;
import com.example.demo.dto.EmployeeResponse;
import com.example.demo.service.EmployeeService;
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
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private EmployeeRequest employeeRequest;
    private EmployeeResponse employeeResponse;

    @BeforeEach
    void setUp() {
        employeeRequest = EmployeeRequest.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .salary(new java.math.BigDecimal("50000.00"))
                .departmentId(UUID.randomUUID())
                .build();

        employeeResponse = EmployeeResponse.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john.doe@example.com")
                .salary(new java.math.BigDecimal("50000.00"))
                .departmentId(UUID.randomUUID())
                .build();
    }

    @Test
    void testCreateEmployee_Success() {

        when(employeeService.createEmployee(any(EmployeeRequest.class)))
                .thenReturn(employeeResponse);


        ResponseEntity<ApiResponse<EmployeeResponse>> response =
                employeeController.createEmployee(employeeRequest);


        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Employee created successfully", response.getBody().message());
        assertEquals(employeeResponse, response.getBody().data());
        verify(employeeService, times(1)).createEmployee(employeeRequest);
    }

    @Test
    void testGetAllEmployees_Success() {

        List<EmployeeResponse> employees = List.of(employeeResponse);
        when(employeeService.getAllEmployees()).thenReturn(employees);


        ResponseEntity<ApiResponse<List<EmployeeResponse>>> response =
                employeeController.getAllEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Employees retrieved successfully", response.getBody().message());
        assertEquals(employees, response.getBody().data());
        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void testGetEmployeeById_Success() {

        UUID id = UUID.randomUUID();
        when(employeeService.getEmployeeById(id)).thenReturn(employeeResponse);


        ResponseEntity<ApiResponse<EmployeeResponse>> response =
                employeeController.getEmployeeById(id);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Employee retrieved successfully", response.getBody().message());
        assertEquals(employeeResponse, response.getBody().data());
        verify(employeeService, times(1)).getEmployeeById(id);
    }

    @Test
    void testUpdateEmployee_Success() {

        UUID id = UUID.randomUUID();
        when(employeeService.updateEmployee(eq(id), any(EmployeeRequest.class)))
                .thenReturn(employeeResponse);


        ResponseEntity<ApiResponse<EmployeeResponse>> response =
                employeeController.updateEmployee(id, employeeRequest);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Employee updated successfully", response.getBody().message());
        assertEquals(employeeResponse, response.getBody().data());
        verify(employeeService, times(1)).updateEmployee(id, employeeRequest);
    }

    @Test
    void testDeleteEmployee_Success() {

        UUID id = UUID.randomUUID();
        doNothing().when(employeeService).deleteEmployee(id);


        ResponseEntity<ApiResponse<Void>> response = employeeController.deleteEmployee(id);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Employee deleted successfully", response.getBody().message());
        assertNull(response.getBody().data());
        verify(employeeService, times(1)).deleteEmployee(id);
    }
}
