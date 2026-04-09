package com.example.demo.service;

import com.example.demo.dto.DepartmentWithEmployeesResponse;
import com.example.demo.dto.SearchRequest;
import com.example.demo.dto.SearchResponse;

import java.util.UUID;


public interface OrganizationService {


    DepartmentWithEmployeesResponse getDepartmentWithEmployees(UUID id);


    SearchResponse searchWithPagination(SearchRequest searchRequest);
}
