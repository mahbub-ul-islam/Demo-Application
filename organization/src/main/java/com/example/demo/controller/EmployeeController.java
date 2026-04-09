package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.EmployeeRequest;
import com.example.demo.dto.EmployeeResponse;
import com.example.demo.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {

        String traceId = getTraceId();
        EmployeeResponse response = employeeService.createEmployee(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(
                response,
                "Employee created successfully",
                "/api/v1/employees",
                traceId,
                HttpStatus.CREATED));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAllEmployees() {
        String traceId = getTraceId();
        List<EmployeeResponse> responses = employeeService.getAllEmployees();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of(
                responses,
                "Employees retrieved successfully",
                "/api/v1/employees",
                traceId,
                HttpStatus.OK));
    }

    @GetMapping(params = "id")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@RequestParam UUID id) {
        String traceId = getTraceId();
        EmployeeResponse response = employeeService.getEmployeeById(id);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of(
                response,
                "Employee retrieved successfully",
                "/api/v1/employees?id=" + id,
                traceId,
                HttpStatus.OK));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @RequestParam UUID id,
            @Valid @RequestBody EmployeeRequest request) {

        String traceId = getTraceId();
        EmployeeResponse response = employeeService.updateEmployee(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of(
                response,
                "Employee updated successfully",
                "/api/v1/employees?id=" + id,
                traceId,
                HttpStatus.OK));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@RequestParam UUID id) {
        String traceId = getTraceId();
        employeeService.deleteEmployee(id);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of(
                null,
                "Employee deleted successfully",
                "/api/v1/employees?id=" + id,
                traceId,
                HttpStatus.OK));
    }

    private String getTraceId() {
        String traceId = MDC.get("traceId");
        return (traceId != null) ? traceId : UUID.randomUUID().toString();
    }
}
