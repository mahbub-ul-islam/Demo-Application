package com.example.demo.service.impl;

import com.example.demo.domain.Department;
import com.example.demo.domain.Employee;
import java.math.BigDecimal;
import com.example.demo.exception.errors.data.DataNotFoundException;
import com.example.demo.exception.errors.business.BusinessException;
import com.example.demo.exception.errors.business.EmailAlreadyExistsException;
import com.example.demo.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmployeeBusinessServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EmployeeBusinessServiceImpl employeeBusinessService;

    private Department department;
    private Employee employee;

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
    }

    @Test
    void testCreate_Success() {

        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);


        Employee result = employeeBusinessService.create(employee);


        assertNotNull(result);
        assertEquals(employee, result);
        verify(employeeRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void testCreate_EmailAlreadyExists_ThrowsException() {

        when(employeeRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> employeeBusinessService.create(employee)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(employeeRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testFindById_Success() {

        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        Optional<Employee> result = employeeBusinessService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(employee, result.get());
        verify(employeeRepository, times(1)).findById(id);
    }

    @Test
    void testFindById_NotFound() {

        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Employee> result = employeeBusinessService.findById(id);


        assertFalse(result.isPresent());
        verify(employeeRepository, times(1)).findById(id);
    }

    @Test
    void testGetEmployeeById_Success() {

        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));


        Employee result = employeeBusinessService.getEmployeeById(id);


        assertNotNull(result);
        assertEquals(employee, result);
        verify(employeeRepository, times(1)).findById(id);
    }

    @Test
    void testGetEmployeeById_NotFound_ThrowsException() {

        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> employeeBusinessService.getEmployeeById(id)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(employeeRepository, times(1)).findById(id);
    }

    @Test
    void testFindAll_Success() {

        List<Employee> employees = List.of(employee);
        when(employeeRepository.findAll()).thenReturn(employees);


        List<Employee> result = employeeBusinessService.findAll();

        assertNotNull(result);
        assertEquals(employees, result);
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testUpdate_Success() {

        UUID id = UUID.randomUUID();
        Employee existingEmployee = Employee.builder()
                .name("Old Name")
                .email("old.email@example.com")
                .salary(BigDecimal.valueOf(40000.0))
                .department(department)
                .build();

        Employee updatedEmployee = Employee.builder()
                .name("New Name")
                .email("new.email@example.com")
                .salary(BigDecimal.valueOf(60000.0))
                .department(department)
                .build();

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.existsByEmail("new.email@example.com")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(existingEmployee);

        Employee result = employeeBusinessService.update(id, updatedEmployee);

        assertNotNull(result);
        assertEquals("New Name", existingEmployee.getName());
        assertEquals("new.email@example.com", existingEmployee.getEmail());
        assertEquals(BigDecimal.valueOf(60000.0), existingEmployee.getSalary());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).save(existingEmployee);
    }

    @Test
    void testUpdate_NotFound_ThrowsException() {

        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> employeeBusinessService.update(id, employee)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testUpdate_EmailAlreadyExists_ThrowsException() {

        UUID id = UUID.randomUUID();
        Employee existingEmployee = Employee.builder()
                .name("Old Name")
                .email("old.email@example.com")
                .salary(BigDecimal.valueOf(40000.0))
                .department(department)
                .build();

        Employee updatedEmployee = Employee.builder()
                .name("New Name")
                .email("new.email@example.com")
                .salary(BigDecimal.valueOf(60000.0))
                .department(department)
                .build();

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.existsByEmail("new.email@example.com")).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> employeeBusinessService.update(id, updatedEmployee)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testDelete_Success() {

        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(any(Employee.class));

        // Act
        employeeBusinessService.deleteById(id);

        // Assert
        verify(employeeRepository, times(2)).findById(id);
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void testDelete_NotFound_ThrowsException() {

        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());


        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> employeeBusinessService.deleteById(id)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }
}
