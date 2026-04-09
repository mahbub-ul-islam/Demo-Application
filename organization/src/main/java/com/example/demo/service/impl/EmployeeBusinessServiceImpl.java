package com.example.demo.service.impl;

import com.example.demo.event.EntityCreatedEvent;
import com.example.demo.event.EntityDeletedEvent;
import com.example.demo.event.EntityReadEvent;
import com.example.demo.event.EntityUpdatedEvent;
import com.example.demo.service.AbstractBaseService;
import com.example.demo.domain.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.exception.errors.data.DataNotFoundException;
import com.example.demo.exception.errors.business.EmailAlreadyExistsException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.slf4j.MDC;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class EmployeeBusinessServiceImpl extends AbstractBaseService<Employee, UUID> {

    private final EmployeeRepository employeeRepository;

    public EmployeeBusinessServiceImpl(EmployeeRepository employeeRepository,
                                     ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
        this.employeeRepository = employeeRepository;
    }


    @Override
    protected Employee executeCreate(Employee entity) {
        validateEmployeeForCreate(entity);
        return employeeRepository.save(entity);
    }

    @Override
    protected Optional<Employee> executeFindById(UUID id) {
        return employeeRepository.findById(id);
    }

    @Override
    protected List<Employee> executeFindAll() {
        return employeeRepository.findAll();
    }
    

    public Employee getEmployeeById(UUID id) {
        return findById(id).orElseThrow(() -> 
            new DataNotFoundException(
                "Employee with ID " + id + " not found",
                MDC.get("traceId"),
                "Cannot perform operation on an employee that does not exist"
            ));
    }

    @Override
    protected Employee executeUpdate(UUID id, Employee entity) {
        Employee existingEmployee = findEmployeeByIdOrThrow(id);
        validateEmployeeForUpdate(existingEmployee, entity);
        updateEmployeeFields(existingEmployee, entity);
        return employeeRepository.save(existingEmployee);
    }

    @Override
    protected void executeDelete(UUID id) {
        Employee employee = findEmployeeByIdOrThrow(id);
        employeeRepository.delete(employee);
    }

    @Override
    protected void publishEvent(Employee entity, String action, String traceId) {
        switch (action) {
            case "CREATED":
                eventPublisher.publishEvent(new EntityCreatedEvent("Employee", entity.getId().toString(), traceId));
                break;
            case "UPDATED":
                eventPublisher.publishEvent(new EntityUpdatedEvent("Employee", entity.getId().toString(), traceId));
                break;
            case "DELETED":
                eventPublisher.publishEvent(new EntityDeletedEvent("Employee", entity.getId().toString(), traceId));
                break;
            case "READ":
                eventPublisher.publishEvent(new EntityReadEvent("Employee", entity.getId().toString(), traceId));
                break;
        }
    }


    private void validateEmployeeForCreate(Employee entity) {
        if (employeeRepository.existsByEmail(entity.getEmail())) {
            throwEmailAlreadyExistsException(entity.getEmail());
        }
    }

    private void validateEmployeeForUpdate(Employee existing, Employee entity) {
        if (!existing.getEmail().equals(entity.getEmail()) && 
            employeeRepository.existsByEmail(entity.getEmail())) {
            throwEmailAlreadyExistsException(entity.getEmail());
        }
    }

    private void updateEmployeeFields(Employee existing, Employee entity) {
        existing.setName(entity.getName());
        existing.setEmail(entity.getEmail());
        existing.setSalary(entity.getSalary());
        existing.setDepartment(entity.getDepartment());
    }

    private Employee findEmployeeByIdOrThrow(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                    "Employee with ID " + id + " not found",
                    MDC.get("traceId"),
                    "Cannot perform operation on an employee that does not exist"
                ));
    }

    private void throwEmailAlreadyExistsException(String email) {
        String traceId = MDC.get("traceId");
        throw new EmailAlreadyExistsException(
                "Employee with email '" + email + "' already exists",
                traceId,
                "Employee emails must be unique"
        );
    }
}
