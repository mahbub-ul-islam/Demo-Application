package com.example.demo.repository;

import com.example.demo.domain.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    

    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.id = :departmentId")
    Department findDepartmentWithEmployees(UUID departmentId);

    Page<Department> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Department findByCode(String code);
    
    boolean existsByName(String name);
    boolean existsByCode(String code);
}
