package com.example.demo.service.impl;

import com.example.demo.domain.Department;
import com.example.demo.dto.DepartmentRequest;
import com.example.demo.dto.DepartmentResponse;
import com.example.demo.mapper.DepartmentMapper;
import com.example.demo.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentBusinessServiceImpl departmentBusinessService;
    private final DepartmentMapper departmentMapper;


    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {

        Department department = departmentMapper.toEntity(request);

        Department savedDepartment = departmentBusinessService.create(department);

        return departmentMapper.toResponse(savedDepartment);
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {

        List<Department> departments = departmentBusinessService.findAll();

        return departmentMapper.toResponseList(departments);
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(UUID id) {

        Department department = departmentBusinessService.getDepartmentById(id);

        return departmentMapper.toResponse(department);
    }

    @Transactional
    public DepartmentResponse updateDepartment(UUID id, DepartmentRequest request) {

        Department department = departmentMapper.toEntity(request);

        Department updatedDepartment = departmentBusinessService.update(id, department);

        return departmentMapper.toResponse(updatedDepartment);
    }

    @Transactional
    public void deleteDepartment(UUID id) {
        departmentBusinessService.deleteById(id);
    }
}
