package com.example.demo.service.impl;

import com.example.demo.event.EntityCreatedEvent;
import com.example.demo.event.EntityDeletedEvent;
import com.example.demo.event.EntityReadEvent;
import com.example.demo.event.EntityUpdatedEvent;
import com.example.demo.service.AbstractBaseService;
import com.example.demo.domain.Department;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.exception.errors.data.DataNotFoundException;
import com.example.demo.exception.errors.data.DataAlreadyExistsException;
import com.example.demo.exception.errors.business.DepartmentCodeAlreadyExistsException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.slf4j.MDC;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class DepartmentBusinessServiceImpl extends AbstractBaseService<Department, UUID> {

    private final DepartmentRepository departmentRepository;

    public DepartmentBusinessServiceImpl(DepartmentRepository departmentRepository,
                                       ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
        this.departmentRepository = departmentRepository;
    }


    @Override
    protected Department executeCreate(Department entity) {
        validateDepartmentForCreate(entity);
        return departmentRepository.save(entity);
    }

    @Override
    protected Optional<Department> executeFindById(UUID id) {
        return departmentRepository.findById(id);
    }

    @Override
    protected List<Department> executeFindAll() {
        return departmentRepository.findAll();
    }
    

    public Department getDepartmentById(UUID id) {
        return findById(id).orElseThrow(() -> 
            new DataNotFoundException(
                "Department with ID " + id + " not found",
                MDC.get("traceId"),
                "Cannot perform operation on a department that does not exist"
            ));
    }

    @Override
    protected Department executeUpdate(UUID id, Department entity) {
        Department existingDepartment = findDepartmentByIdOrThrow(id);
        validateDepartmentForUpdate(existingDepartment, entity);
        updateDepartmentFields(existingDepartment, entity);
        return departmentRepository.save(existingDepartment);
    }

    @Override
    protected void executeDelete(UUID id) {
        Department department = findDepartmentByIdOrThrow(id);
        departmentRepository.delete(department);
    }

    @Override
    protected void publishEvent(Department entity, String action, String traceId) {
        switch (action) {
            case "CREATED":
                eventPublisher.publishEvent(new EntityCreatedEvent("Department", entity.getId().toString(), traceId));
                break;
            case "UPDATED":
                eventPublisher.publishEvent(new EntityUpdatedEvent("Department", entity.getId().toString(), traceId));
                break;
            case "DELETED":
                eventPublisher.publishEvent(new EntityDeletedEvent("Department", entity.getId().toString(), traceId));
                break;
            case "READ":
                eventPublisher.publishEvent(new EntityReadEvent("Department", entity.getId().toString(), traceId));
                break;
        }
    }


    private void validateDepartmentForCreate(Department entity) {
        if (departmentRepository.existsByName(entity.getName())) {
            throwDepartmentAlreadyExistsException(entity.getName());
        }
        if (departmentRepository.existsByCode(entity.getCode())) {
            throwDepartmentCodeAlreadyExistsException(entity.getCode());
        }
    }

    private void validateDepartmentForUpdate(Department existing, Department entity) {
        if (!existing.getName().equals(entity.getName()) && 
            departmentRepository.existsByName(entity.getName())) {
            throwDepartmentAlreadyExistsException(entity.getName());
        }
        if (!existing.getCode().equals(entity.getCode()) && 
            departmentRepository.existsByCode(entity.getCode())) {
            throwDepartmentCodeAlreadyExistsException(entity.getCode());
        }
    }

    private void updateDepartmentFields(Department existing, Department entity) {
        existing.setName(entity.getName());
        existing.setCode(entity.getCode());
    }

    private Department findDepartmentByIdOrThrow(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                    "Department with ID " + id + " not found",
                    MDC.get("traceId"),
                    "Cannot perform operation on a department that does not exist"
                ));
    }

    private void throwDepartmentAlreadyExistsException(String departmentName) {
        String traceId = MDC.get("traceId");
        throw new DataAlreadyExistsException(
                "Department with name '" + departmentName + "' already exists",
                traceId,
                "Department names must be unique"
        );
    }
    
    private void throwDepartmentCodeAlreadyExistsException(String departmentCode) {
        String traceId = MDC.get("traceId");
        throw new DepartmentCodeAlreadyExistsException(
                "Department with code '" + departmentCode + "' already exists",
                traceId,
                "Department codes must be unique"
        );
    }
}
