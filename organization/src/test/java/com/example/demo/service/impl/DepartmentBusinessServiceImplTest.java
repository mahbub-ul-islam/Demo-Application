package com.example.demo.service.impl;

import com.example.demo.domain.Department;
import com.example.demo.exception.errors.data.DataAlreadyExistsException;
import com.example.demo.exception.errors.data.DataNotFoundException;
import com.example.demo.exception.errors.business.BusinessException;
import com.example.demo.exception.errors.business.DepartmentCodeAlreadyExistsException;
import com.example.demo.repository.DepartmentRepository;
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
class DepartmentBusinessServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DepartmentBusinessServiceImpl departmentBusinessService;

    private Department department;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .name("Engineering")
                .code("ENG")
                .build();
    }

    @Test
    void testCreate_Success() {

        when(departmentRepository.existsByName(anyString())).thenReturn(false);
        when(departmentRepository.existsByCode(anyString())).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(department);


        Department result = departmentBusinessService.create(department);


        assertNotNull(result);
        assertEquals(department, result);
        verify(departmentRepository, times(1)).existsByName("Engineering");
        verify(departmentRepository, times(1)).existsByCode("ENG");
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void testCreate_NameAlreadyExists_ThrowsException() {

        when(departmentRepository.existsByName("Engineering")).thenReturn(true);


        DataAlreadyExistsException exception = assertThrows(
                DataAlreadyExistsException.class,
                () -> departmentBusinessService.create(department)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(departmentRepository, times(1)).existsByName("Engineering");
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void testCreate_CodeAlreadyExists_ThrowsException() {

        when(departmentRepository.existsByName("Engineering")).thenReturn(false);
        when(departmentRepository.existsByCode("ENG")).thenReturn(true);


        DepartmentCodeAlreadyExistsException exception = assertThrows(
                DepartmentCodeAlreadyExistsException.class,
                () -> departmentBusinessService.create(department)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(departmentRepository, times(1)).existsByName("Engineering");
        verify(departmentRepository, times(1)).existsByCode("ENG");
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void testFindById_Success() {

        UUID id = UUID.randomUUID();
        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));


        Optional<Department> result = departmentBusinessService.findById(id);


        assertTrue(result.isPresent());
        assertEquals(department, result.get());
        verify(departmentRepository, times(1)).findById(id);
    }

    @Test
    void testFindById_NotFound() {

        UUID id = UUID.randomUUID();
        when(departmentRepository.findById(id)).thenReturn(Optional.empty());


        Optional<Department> result = departmentBusinessService.findById(id);


        assertFalse(result.isPresent());
        verify(departmentRepository, times(1)).findById(id);
    }

    @Test
    void testGetDepartmentById_Success() {

        UUID id = UUID.randomUUID();
        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));


        Department result = departmentBusinessService.getDepartmentById(id);


        assertNotNull(result);
        assertEquals(department, result);
        verify(departmentRepository, times(1)).findById(id);
    }

    @Test
    void testGetDepartmentById_NotFound_ThrowsException() {

        UUID id = UUID.randomUUID();
        when(departmentRepository.findById(id)).thenReturn(Optional.empty());


        DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> departmentBusinessService.getDepartmentById(id)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(departmentRepository, times(1)).findById(id);
    }

    @Test
    void testFindAll_Success() {

        List<Department> departments = List.of(department);
        when(departmentRepository.findAll()).thenReturn(departments);


        List<Department> result = departmentBusinessService.findAll();


        assertNotNull(result);
        assertEquals(departments, result);
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void testUpdate_Success() {

        UUID id = UUID.randomUUID();
        Department existingDepartment = Department.builder()
                .name("Old Name")
                .code("OLD")
                .build();
        existingDepartment.setId(id);

        Department updatedDepartment = Department.builder()
                .name("New Name")
                .code("NEW")
                .build();

        when(departmentRepository.findById(id)).thenReturn(Optional.of(existingDepartment));
        when(departmentRepository.existsByName("New Name")).thenReturn(false);
        when(departmentRepository.existsByCode("NEW")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(existingDepartment);

        Department result = departmentBusinessService.update(id, updatedDepartment);


        assertNotNull(result);
        assertEquals("New Name", existingDepartment.getName());
        assertEquals("NEW", existingDepartment.getCode());
        verify(departmentRepository, times(1)).findById(id);
        verify(departmentRepository, times(1)).save(existingDepartment);
    }

    @Test
    void testUpdate_NotFound_ThrowsException() {

        UUID id = UUID.randomUUID();
        when(departmentRepository.findById(id)).thenReturn(Optional.empty());


        DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> departmentBusinessService.update(id, department)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(departmentRepository, times(1)).findById(id);
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void testDelete_Success() {

        UUID id = UUID.randomUUID();
        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));
        doNothing().when(departmentRepository).delete(any(Department.class));


        departmentBusinessService.deleteById(id);


        verify(departmentRepository, times(2)).findById(id);
        verify(departmentRepository, times(1)).delete(department);
    }

    @Test
    void testDelete_NotFound_ThrowsException() {

        UUID id = UUID.randomUUID();
        when(departmentRepository.findById(id)).thenReturn(Optional.empty());


        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> departmentBusinessService.deleteById(id)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(departmentRepository, times(1)).findById(id);
        verify(departmentRepository, never()).delete(any(Department.class));
    }
}
