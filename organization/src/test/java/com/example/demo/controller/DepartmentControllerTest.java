package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.DepartmentRequest;
import com.example.demo.dto.DepartmentResponse;
import com.example.demo.service.DepartmentService;
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
class DepartmentControllerTest {

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    private DepartmentRequest departmentRequest;
    private DepartmentResponse departmentResponse;

    @BeforeEach
    void setUp() {
        departmentRequest = DepartmentRequest.builder()
                .name("Engineering")
                .code("ENG")
                .build();

        departmentResponse = DepartmentResponse.builder()
                .id(UUID.randomUUID())
                .name("Engineering")
                .code("ENG")
                .build();
    }

    @Test
    void testCreateDepartment_Success() {

        when(departmentService.createDepartment(any(DepartmentRequest.class)))
                .thenReturn(departmentResponse);


        ResponseEntity<ApiResponse<DepartmentResponse>> response =
                departmentController.createDepartment(departmentRequest);


        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Department created successfully", response.getBody().message());
        assertEquals(departmentResponse, response.getBody().data());
        verify(departmentService, times(1)).createDepartment(departmentRequest);
    }

    @Test
    void testGetAllDepartments_Success() {

        List<DepartmentResponse> departments = List.of(departmentResponse);
        when(departmentService.getAllDepartments()).thenReturn(departments);


        ResponseEntity<ApiResponse<List<DepartmentResponse>>> response =
                departmentController.getAllDepartments();


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Departments retrieved successfully", response.getBody().message());
        assertEquals(departments, response.getBody().data());
        verify(departmentService, times(1)).getAllDepartments();
    }

    @Test
    void testGetDepartmentById_Success() {

        UUID id = UUID.randomUUID();
        when(departmentService.getDepartmentById(id)).thenReturn(departmentResponse);


        ResponseEntity<ApiResponse<DepartmentResponse>> response =
                departmentController.getDepartmentById(id);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Department retrieved successfully", response.getBody().message());
        assertEquals(departmentResponse, response.getBody().data());
        verify(departmentService, times(1)).getDepartmentById(id);
    }

    @Test
    void testUpdateDepartment_Success() {

        UUID id = UUID.randomUUID();
        when(departmentService.updateDepartment(eq(id), any(DepartmentRequest.class)))
                .thenReturn(departmentResponse);


        ResponseEntity<ApiResponse<DepartmentResponse>> response =
                departmentController.updateDepartment(id, departmentRequest);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Department updated successfully", response.getBody().message());
        assertEquals(departmentResponse, response.getBody().data());
        verify(departmentService, times(1)).updateDepartment(id, departmentRequest);
    }

    @Test
    void testDeleteDepartment_Success() {

        UUID id = UUID.randomUUID();
        doNothing().when(departmentService).deleteDepartment(id);


        ResponseEntity<ApiResponse<Void>> response = departmentController.deleteDepartment(id);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Department deleted successfully", response.getBody().message());
        assertNull(response.getBody().data());
        verify(departmentService, times(1)).deleteDepartment(id);
    }
}
