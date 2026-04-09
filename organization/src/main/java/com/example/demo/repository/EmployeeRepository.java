package com.example.demo.repository;

import com.example.demo.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {


    Page<Employee> findByNameContainingIgnoreCase(String name, Pageable pageable);


    Page<Employee> findByNameContainingIgnoreCaseAndDepartmentId(String name, UUID departmentId, Pageable pageable);


    Page<Employee> findByDepartmentId(UUID departmentId, Pageable pageable);


    @Query("SELECT e FROM Employee e JOIN e.department d WHERE d.code = :departmentCode")
    Page<Employee> findByDepartmentCode(String departmentCode, Pageable pageable);
    
    boolean existsByEmail(String email);
}
