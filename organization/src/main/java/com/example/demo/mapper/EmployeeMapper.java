package com.example.demo.mapper;

import com.example.demo.domain.Department;
import com.example.demo.domain.Employee;
import com.example.demo.dto.EmployeeRequest;
import com.example.demo.dto.EmployeeResponse;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    default Employee toEntity(EmployeeRequest request, Department department) {
        return Employee.builder()
                .name(request.getName())
                .email(request.getEmail())
                .salary(request.getSalary())
                .department(department)
                .build();
    }

    default void updateEntity(EmployeeRequest request, Employee employee, Department department) {
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setSalary(request.getSalary());
        employee.setDepartment(department);
    }

    default EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .salary(employee.getSalary())
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .build();
    }

    default List<EmployeeResponse> toResponseList(List<Employee> employees) {
        return employees.stream()
                .map(this::toResponse)
                .toList();
    }
}
