package com.example.demo.service.impl;

import com.example.demo.domain.Department;
import com.example.demo.domain.Employee;
import com.example.demo.dto.EmployeeRequest;
import com.example.demo.dto.EmployeeResponse;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.service.EmployeeService;
import com.example.demo.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.MDC;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeBusinessServiceImpl employeeBusinessService;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;


    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        Department department = validateAndGetDepartment(request.getDepartmentId());
        Employee employee = employeeMapper.toEntity(request, department);

        Employee savedEmployee = employeeBusinessService.create(employee);

        return employeeMapper.toResponse(savedEmployee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {

        List<Employee> employees = employeeBusinessService.findAll();

        return employeeMapper.toResponseList(employees);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(UUID id) {

        Employee employee = employeeBusinessService.getEmployeeById(id);

        return employeeMapper.toResponse(employee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(UUID id, EmployeeRequest request) {

        Department department = validateAndGetDepartment(request.getDepartmentId());

        Employee employee = employeeMapper.toEntity(request, department);

        Employee updatedEmployee = employeeBusinessService.update(id, employee);

        return employeeMapper.toResponse(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(UUID id) {

        employeeBusinessService.deleteById(id);
    }

    private Department validateAndGetDepartment(UUID departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new com.example.demo.exception.errors.data.DataNotFoundException(
                        "Department with ID " + departmentId + " not found",
                        MDC.get("traceId"),
                        "Cannot perform operation for a department that does not exist"
                ));
    }
}
