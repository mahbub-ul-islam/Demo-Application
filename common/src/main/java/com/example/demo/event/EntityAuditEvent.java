package com.example.demo.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class EntityAuditEvent {
    private String entityName;
    private String entityId;
    private String action;
    private String traceId;
    private LocalDateTime timestamp;
    private String details;
    private String performedBy;
    
    public EntityAuditEvent(String entityName, String entityId, String action, String traceId) {
        this.entityName = entityName;
        this.entityId = entityId;
        this.action = action;
        this.traceId = traceId;
        this.timestamp = LocalDateTime.now();
    }
}
