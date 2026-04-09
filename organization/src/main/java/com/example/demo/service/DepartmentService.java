package com.example.demo.service;

import com.example.demo.dto.DepartmentRequest;
import com.example.demo.dto.DepartmentResponse;

import java.util.List;
import java.util.UUID;


public interface DepartmentService {


    DepartmentResponse createDepartment(DepartmentRequest request);


    List<DepartmentResponse> getAllDepartments();


    DepartmentResponse getDepartmentById(UUID id);


    DepartmentResponse updateDepartment(UUID id, DepartmentRequest request);


    void deleteDepartment(UUID id);
}
