package com.example.demo.service;

import com.example.demo.exception.errors.business.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
public abstract class AbstractBaseService<T, ID> implements BaseService<T, ID> {

    protected final ApplicationEventPublisher eventPublisher;

    protected AbstractBaseService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    protected abstract T executeCreate(T entity);
    protected abstract Optional<T> executeFindById(ID id);
    protected abstract List<T> executeFindAll();
    protected abstract T executeUpdate(ID id, T entity);
    protected abstract void executeDelete(ID id);


    @Override
    public T create(T entity) {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }
        final String finalTraceId = traceId;
        log.info("[SERVICE_START] Operation: CREATE, TraceId: {}", finalTraceId);
        
        try {
            validate(entity, finalTraceId);
            T result = executeCreate(entity);
            publishEvent(result, "CREATED", finalTraceId);
            
            log.info("[SERVICE_SUCCESS] Operation: CREATE, TraceId: {}", finalTraceId);
            return result;
        } catch (Exception e) {
            throw handleException(e, "creation", finalTraceId);
        }
    }


    @Override
    public Optional<T> findById(ID id) {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }
        final String finalTraceId = traceId;
        log.info("[SERVICE_START] Operation: FIND_BY_ID, ID: {}, TraceId: {}", id, finalTraceId);
        
        try {
            if (id == null) {
                throw new BusinessException("ID cannot be null", HttpStatus.BAD_REQUEST, traceId);
            }
            
            Optional<T> result = executeFindById(id);
            result.ifPresent(entity -> publishEvent(entity, "READ", finalTraceId));
            
            log.info("[SERVICE_SUCCESS] Operation: FIND_BY_ID, ID: {}, Found: {}, TraceId: {}", id, result.isPresent(), finalTraceId);
            return result;
        } catch (Exception e) {
            throw handleException(e, "findById", finalTraceId);
        }
    }


    @Override
    public List<T> findAll() {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }
        final String finalTraceId = traceId;
        log.info("[SERVICE_START] Operation: FIND_ALL, TraceId: {}", finalTraceId);
        
        try {
            List<T> result = executeFindAll();
            result.forEach(entity -> publishEvent(entity, "READ", finalTraceId));
            
            log.info("[SERVICE_SUCCESS] Operation: FIND_ALL, Count: {}, TraceId: {}", result.size(), finalTraceId);
            return result;
        } catch (Exception e) {
            throw handleException(e, "findAll", finalTraceId);
        }
    }


    @Override
    public T update(ID id, T entity) {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }
        final String finalTraceId = traceId;
        log.info("[SERVICE_START] Operation: UPDATE, ID: {}, TraceId: {}", id, finalTraceId);
        
        try {
            validate(entity, finalTraceId);
            if (id == null) {
                throw new BusinessException("ID cannot be null", HttpStatus.BAD_REQUEST, finalTraceId);
            }
            
            T result = executeUpdate(id, entity);
            publishEvent(result, "UPDATED", finalTraceId);
            
            log.info("[SERVICE_SUCCESS] Operation: UPDATE, ID: {}, TraceId: {}", id, finalTraceId);
            return result;
        } catch (Exception e) {
            throw handleException(e, "update", finalTraceId);
        }
    }


    @Override
    public void deleteById(ID id) {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }
        final String finalTraceId = traceId;
        log.info("[SERVICE_START] Operation: DELETE, ID: {}, TraceId: {}", id, finalTraceId);
        
        try {
            if (id == null) {
                throw new BusinessException("ID cannot be null", HttpStatus.BAD_REQUEST, finalTraceId);
            }
            
            T entity = executeFindById(id).orElseThrow(() -> 
                new BusinessException("Entity not found for deletion", HttpStatus.NOT_FOUND, finalTraceId));
            
            executeDelete(id);
            publishEvent(entity, "DELETED", finalTraceId);
            
            log.info("[SERVICE_SUCCESS] Operation: DELETE, ID: {}, TraceId: {}", id, finalTraceId);
        } catch (Exception e) {
            throw handleException(e, "deletion", finalTraceId);
        }
    }


    private RuntimeException handleException(Exception e, String operation, String traceId) {
        if (e instanceof BusinessException) {
            BusinessException be = (BusinessException) e;
            log.warn("[SERVICE_BUSINESS_FAIL] Message: {}, TraceId: {}", be.getMessage(), traceId);
            throw be;
        } else if (e instanceof com.example.demo.exception.errors.ApplicationException) {
            com.example.demo.exception.errors.ApplicationException ae = (com.example.demo.exception.errors.ApplicationException) e;
            log.warn("[SERVICE_APPLICATION_FAIL] Message: {}, TraceId: {}", ae.getMessage(), traceId);
            throw ae;
        } else {
            log.error("[SERVICE_SYSTEM_FAIL] Message: {}, TraceId: {}", e.getMessage(), traceId, e);
            throw new BusinessException("Internal Server Error during " + operation, HttpStatus.INTERNAL_SERVER_ERROR, traceId);
        }
    }


    protected void validate(T entity, String traceId) {
        if (entity == null) {
            throw new BusinessException("Entity cannot be null", HttpStatus.BAD_REQUEST, traceId);
        }
    }


    protected void publishEvent(T entity, String action, String traceId) {
        log.debug("[EVENT_PUBLISHED] Action: {}, Entity: {}, TraceId: {}", action, entity.getClass().getSimpleName(), traceId);
    }
}
