package com.example.demo.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {


    @Pattern(regexp = "departments|employees", message = "Type must be 'departments' or 'employees'")
    private String type;

    private String searchTerm;

    private UUID departmentId;

    private String departmentCode;

    private PageRequest pageRequest;
}
