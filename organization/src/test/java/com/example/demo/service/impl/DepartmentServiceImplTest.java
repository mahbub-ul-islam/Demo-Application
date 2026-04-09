package com.example.demo.service.impl;

import com.example.demo.domain.Department;
import com.example.demo.dto.DepartmentRequest;
import com.example.demo.dto.DepartmentResponse;
import com.example.demo.mapper.DepartmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentBusinessServiceImpl departmentBusinessService;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private DepartmentRequest departmentRequest;
    private Department department;
    private DepartmentResponse departmentResponse;

    @BeforeEach
    void setUp() {
        departmentRequest = DepartmentRequest.builder()
                .name("Engineering")
                .code("ENG")
                .build();

        department = Department.builder()
                .name("Engineering")
                .code("ENG")
                .build();

        departmentResponse = DepartmentResponse.builder()
                .id(department.getId())
                .name("Engineering")
                .code("ENG")
                .build();
    }

    @Test
    void testCreateDepartment_Success() {

        when(departmentMapper.toEntity(departmentRequest)).thenReturn(department);
        when(departmentBusinessService.create(department)).thenReturn(department);
        when(departmentMapper.toResponse(department)).thenReturn(departmentResponse);


        DepartmentResponse result = departmentService.createDepartment(departmentRequest);


        assertNotNull(result);
        assertEquals(departmentResponse, result);
        verify(departmentMapper, times(1)).toEntity(departmentRequest);
        verify(departmentBusinessService, times(1)).create(department);
        verify(departmentMapper, times(1)).toResponse(department);
    }

    @Test
    void testGetAllDepartments_Success() {

        List<Department> departments = List.of(department);
        List<DepartmentResponse> responses = List.of(departmentResponse);
        when(departmentBusinessService.findAll()).thenReturn(departments);
        when(departmentMapper.toResponseList(departments)).thenReturn(responses);


        List<DepartmentResponse> result = departmentService.getAllDepartments();


        assertNotNull(result);
        assertEquals(responses, result);
        verify(departmentBusinessService, times(1)).findAll();
        verify(departmentMapper, times(1)).toResponseList(departments);
    }

    @Test
    void testGetDepartmentById_Success() {

        UUID id = UUID.randomUUID();
        when(departmentBusinessService.getDepartmentById(id)).thenReturn(department);
        when(departmentMapper.toResponse(department)).thenReturn(departmentResponse);


        DepartmentResponse result = departmentService.getDepartmentById(id);


        assertNotNull(result);
        assertEquals(departmentResponse, result);
        verify(departmentBusinessService, times(1)).getDepartmentById(id);
        verify(departmentMapper, times(1)).toResponse(department);
    }

    @Test
    void testUpdateDepartment_Success() {

        UUID id = UUID.randomUUID();
        when(departmentMapper.toEntity(departmentRequest)).thenReturn(department);
        when(departmentBusinessService.update(id, department)).thenReturn(department);
        when(departmentMapper.toResponse(department)).thenReturn(departmentResponse);


        DepartmentResponse result = departmentService.updateDepartment(id, departmentRequest);


        assertNotNull(result);
        assertEquals(departmentResponse, result);
        verify(departmentMapper, times(1)).toEntity(departmentRequest);
        verify(departmentBusinessService, times(1)).update(id, department);
        verify(departmentMapper, times(1)).toResponse(department);
    }

    @Test
    void testDeleteDepartment_Success() {

        UUID id = UUID.randomUUID();
        doNothing().when(departmentBusinessService).deleteById(id);


        departmentService.deleteDepartment(id);


        verify(departmentBusinessService, times(1)).deleteById(id);
    }
}
