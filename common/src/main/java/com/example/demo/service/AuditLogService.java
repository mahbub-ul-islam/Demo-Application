package com.example.demo.service;

import com.example.demo.event.EntityCreatedEvent;
import com.example.demo.event.EntityDeletedEvent;
import com.example.demo.event.EntityReadEvent;
import com.example.demo.event.EntityUpdatedEvent;

public interface AuditLogService {

    void handleEntityCreated(EntityCreatedEvent event);

    void handleEntityUpdated(EntityUpdatedEvent event);

    void handleEntityDeleted(EntityDeletedEvent event);

    void handleEntityRead(EntityReadEvent event);
}
