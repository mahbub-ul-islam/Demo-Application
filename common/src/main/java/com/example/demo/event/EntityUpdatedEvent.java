package com.example.demo.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EntityUpdatedEvent extends EntityAuditEvent {
    
    public EntityUpdatedEvent(String entityName, String entityId, String traceId, String details) {
        super(entityName, entityId, "UPDATED", traceId);
        setDetails(details);
    }
    
    public EntityUpdatedEvent(String entityName, String entityId, String traceId) {
        this(entityName, entityId, traceId, null);
    }
}
