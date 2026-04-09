package com.example.demo.service;

import com.example.demo.dto.EmployeeRequest;
import com.example.demo.dto.EmployeeResponse;

import java.util.List;
import java.util.UUID;


public interface EmployeeService {


    EmployeeResponse createEmployee(EmployeeRequest request);


    List<EmployeeResponse> getAllEmployees();


    EmployeeResponse getEmployeeById(UUID id);


    EmployeeResponse updateEmployee(UUID id, EmployeeRequest request);


    void deleteEmployee(UUID id);
}
