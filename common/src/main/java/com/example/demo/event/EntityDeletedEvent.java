package com.example.demo.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EntityDeletedEvent extends EntityAuditEvent {
    
    public EntityDeletedEvent(String entityName, String entityId, String traceId, String details) {
        super(entityName, entityId, "DELETED", traceId);
        setDetails(details);
    }
    
    public EntityDeletedEvent(String entityName, String entityId, String traceId) {
        this(entityName, entityId, traceId, null);
    }
}
