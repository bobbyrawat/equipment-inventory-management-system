package com.example.inventory.service;

import com.example.inventory.dto.AuditDtos;
import com.example.inventory.entity.User;
import java.util.List;

public interface AuditService {
    void record(User user, String action, String entityType, Long entityId, String description);
    List<AuditDtos.Response> getAll();
}
