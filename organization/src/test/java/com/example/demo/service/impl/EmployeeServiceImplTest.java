package com.example.demo.service.impl;

import com.example.demo.domain.Department;
import com.example.demo.domain.Employee;
import com.example.demo.dto.EmployeeRequest;
import com.example.demo.dto.EmployeeResponse;
import com.example.demo.exception.errors.data.DataNotFoundException;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeBusinessServiceImpl employeeBusinessService;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeRequest employeeRequest;
    private Employee employee;
    private Department department;
    private EmployeeResponse employeeResponse;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .name("Engineering")
                .code("ENG")
                .build();

        employeeRequest = EmployeeRequest.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .salary(new java.math.BigDecimal("50000.00"))
                .departmentId(department.getId())
                .build();

        employee = Employee.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .salary(new java.math.BigDecimal("50000.00"))
                .department(department)
                .build();

        employeeResponse = EmployeeResponse.builder()
                .id(employee.getId())
                .name("John Doe")
                .email("john.doe@example.com")
                .salary(new java.math.BigDecimal("50000.00"))
                .departmentId(department.getId())
                .departmentName("Engineering")
                .build();
    }

    @Test
    void testCreateEmployee_Success() {

        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(employeeMapper.toEntity(employeeRequest, department)).thenReturn(employee);
        when(employeeBusinessService.create(employee)).thenReturn(employee);
        when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);


        EmployeeResponse result = employeeService.createEmployee(employeeRequest);

        assertNotNull(result);
        assertEquals(employeeResponse, result);
        verify(departmentRepository, times(1)).findById(department.getId());
        verify(employeeMapper, times(1)).toEntity(employeeRequest, department);
        verify(employeeBusinessService, times(1)).create(employee);
        verify(employeeMapper, times(1)).toResponse(employee);
    }

    @Test
    void testCreateEmployee_DepartmentNotFound_ThrowsException() {

        when(departmentRepository.findById(department.getId())).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> employeeService.createEmployee(employeeRequest)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(departmentRepository, times(1)).findById(department.getId());
        verify(employeeMapper, never()).toEntity(any(), any());
        verify(employeeBusinessService, never()).create(any(Employee.class));
    }

    @Test
    void testGetAllEmployees_Success() {

        List<Employee> employees = List.of(employee);
        List<EmployeeResponse> responses = List.of(employeeResponse);
        when(employeeBusinessService.findAll()).thenReturn(employees);
        when(employeeMapper.toResponseList(employees)).thenReturn(responses);


        List<EmployeeResponse> result = employeeService.getAllEmployees();


        assertNotNull(result);
        assertEquals(responses, result);
        verify(employeeBusinessService, times(1)).findAll();
        verify(employeeMapper, times(1)).toResponseList(employees);
    }

    @Test
    void testGetEmployeeById_Success() {

        UUID id = UUID.randomUUID();
        when(employeeBusinessService.getEmployeeById(id)).thenReturn(employee);
        when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);


        EmployeeResponse result = employeeService.getEmployeeById(id);


        assertNotNull(result);
        assertEquals(employeeResponse, result);
        verify(employeeBusinessService, times(1)).getEmployeeById(id);
        verify(employeeMapper, times(1)).toResponse(employee);
    }

    @Test
    void testUpdateEmployee_Success() {

        UUID id = UUID.randomUUID();
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(employeeMapper.toEntity(employeeRequest, department)).thenReturn(employee);
        when(employeeBusinessService.update(id, employee)).thenReturn(employee);
        when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);


        EmployeeResponse result = employeeService.updateEmployee(id, employeeRequest);


        assertNotNull(result);
        assertEquals(employeeResponse, result);
        verify(departmentRepository, times(1)).findById(department.getId());
        verify(employeeMapper, times(1)).toEntity(employeeRequest, department);
        verify(employeeBusinessService, times(1)).update(id, employee);
        verify(employeeMapper, times(1)).toResponse(employee);
    }

    @Test
    void testUpdateEmployee_DepartmentNotFound_ThrowsException() {

        UUID id = UUID.randomUUID();
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.empty());


        DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> employeeService.updateEmployee(id, employeeRequest)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(departmentRepository, times(1)).findById(department.getId());
        verify(employeeMapper, never()).toEntity(any(), any());
        verify(employeeBusinessService, never()).update(any(UUID.class), any(Employee.class));
    }

    @Test
    void testDeleteEmployee_Success() {

        UUID id = UUID.randomUUID();
        doNothing().when(employeeBusinessService).deleteById(id);

        employeeService.deleteEmployee(id);

        verify(employeeBusinessService, times(1)).deleteById(id);
    }
}
