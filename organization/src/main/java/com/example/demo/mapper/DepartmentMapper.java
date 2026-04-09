package com.example.demo.mapper;

import com.example.demo.domain.Department;
import com.example.demo.dto.DepartmentRequest;
import com.example.demo.dto.DepartmentResponse;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    default Department toEntity(DepartmentRequest request) {
        return Department.builder()
                .name(request.getName())
                .code(request.getCode())
                .build();
    }

    default void updateEntity(DepartmentRequest request, Department department) {
        department.setName(request.getName());
        department.setCode(request.getCode());
    }

    default DepartmentResponse toResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .build();
    }

    default List<DepartmentResponse> toResponseList(List<Department> departments) {
        return departments.stream()
                .map(this::toResponse)
                .toList();
    }
}
