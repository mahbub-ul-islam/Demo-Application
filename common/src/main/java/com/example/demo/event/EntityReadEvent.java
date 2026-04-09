package com.example.demo.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EntityReadEvent extends EntityAuditEvent {
    
    public EntityReadEvent(String entityName, String entityId, String traceId, String details) {
        super(entityName, entityId, "READ", traceId);
        setDetails(details);
    }
    
    public EntityReadEvent(String entityName, String entityId, String traceId) {
        this(entityName, entityId, traceId, null);
    }
}
