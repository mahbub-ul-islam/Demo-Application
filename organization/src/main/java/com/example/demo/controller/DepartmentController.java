package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.DepartmentRequest;
import com.example.demo.dto.DepartmentResponse;
import com.example.demo.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(
            @Valid @RequestBody DepartmentRequest request) {

        DepartmentResponse response = departmentService.createDepartment(request);
        String traceId = getTraceId();

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(
                response,
                "Department created successfully",
                "/api/v1/departments",
                traceId,
                HttpStatus.CREATED));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAllDepartments() {
        List<DepartmentResponse> responses = departmentService.getAllDepartments();
        String traceId = getTraceId();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of(
                responses,
                "Departments retrieved successfully",
                "/api/v1/departments",
                traceId,
                HttpStatus.OK));
    }

    @GetMapping(params = "id")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(@RequestParam UUID id) {
        DepartmentResponse response = departmentService.getDepartmentById(id);
        String traceId = getTraceId();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of(
                response,
                "Department retrieved successfully",
                "/api/v1/departments?id=" + id,
                traceId,
                HttpStatus.OK));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @RequestParam UUID id,
            @Valid @RequestBody DepartmentRequest request) {

        DepartmentResponse response = departmentService.updateDepartment(id, request);
        String traceId = getTraceId();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of(
                response,
                "Department updated successfully",
                "/api/v1/departments?id=" + id,
                traceId,
                HttpStatus.OK));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@RequestParam UUID id) {
        departmentService.deleteDepartment(id);
        String traceId = getTraceId();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.of(
                null,
                "Department deleted successfully",
                "/api/v1/departments?id=" + id,
                traceId,
                HttpStatus.OK));
    }

    private String getTraceId() {
        String traceId = MDC.get("traceId");
        return (traceId != null) ? traceId : UUID.randomUUID().toString();
    }
}
