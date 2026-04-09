package com.example.demo.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EntityCreatedEvent extends EntityAuditEvent {
    
    public EntityCreatedEvent(String entityName, String entityId, String traceId, String details) {
        super(entityName, entityId, "CREATED", traceId);
        setDetails(details);
    }
    
    public EntityCreatedEvent(String entityName, String entityId, String traceId) {
        this(entityName, entityId, traceId, null);
    }
}
