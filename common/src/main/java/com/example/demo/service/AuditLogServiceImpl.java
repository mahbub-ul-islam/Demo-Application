package com.example.demo.service;

import com.example.demo.domain.AuditLog;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.event.EntityAuditEvent;
import com.example.demo.event.EntityCreatedEvent;
import com.example.demo.event.EntityDeletedEvent;
import com.example.demo.event.EntityReadEvent;
import com.example.demo.event.EntityUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }


    @EventListener
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleEntityCreated(EntityCreatedEvent event) {
        logToConsoleAndDatabase(event, "CREATED");
    }


    @EventListener
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleEntityUpdated(EntityUpdatedEvent event) {
        logToConsoleAndDatabase(event, "UPDATED");
    }


    @EventListener
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleEntityDeleted(EntityDeletedEvent event) {
        logToConsoleAndDatabase(event, "DELETED");
    }


    @EventListener
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleEntityRead(EntityReadEvent event) {
        logToConsoleAndDatabase(event, "READ");
    }


    private void logToConsoleAndDatabase(EntityAuditEvent event, String action) {

        log.info("[AUDIT] Action: {}, Entity: {}, ID: {}, TraceId: {}, Timestamp: {}, Details: {}", 
                action, event.getEntityName(), event.getEntityId(), event.getTraceId(), 
                event.getTimestamp(), event.getDetails());

        try {
            AuditLog auditLog = AuditLog.builder()
                    .entityName(event.getEntityName())
                    .entityId(event.getEntityId())
                    .action(action)
                    .traceId(event.getTraceId())
                    .details(event.getDetails())
                    .createdBy("SYSTEM")
                    .createdAt(event.getTimestamp())
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("[AUDIT_PERSISTED] Entity: {}, ID: {}, Action: {}", 
                    event.getEntityName(), event.getEntityId(), action);
        } catch (Exception e) {
            log.error("[AUDIT_FAILED] Failed to persist audit record for Entity: {}, ID: {}, Error: {}", 
                    event.getEntityName(), event.getEntityId(), e.getMessage(), e);
        }
    }

}
