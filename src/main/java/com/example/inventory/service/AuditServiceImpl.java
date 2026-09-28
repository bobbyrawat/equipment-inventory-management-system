package com.example.inventory.service;

import com.example.inventory.dto.AuditDtos;
import com.example.inventory.entity.AuditLog;
import com.example.inventory.entity.User;
import com.example.inventory.repository.AuditLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditServiceImpl implements AuditService {
    private final AuditLogRepository auditLogRepository;

    @Override
    public void record(User user, String action, String entityType, Long entityId, String description) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        auditLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditDtos.Response> getAll() {
        return auditLogRepository.findAllByOrderByTimestampDesc().stream()
                .map(log -> new AuditDtos.Response(log.getId(), log.getUser() == null ? null : log.getUser().getId(),
                        log.getUser() == null ? null : log.getUser().getUsername(), log.getAction(),
                        log.getEntityType(), log.getEntityId(), log.getTimestamp(), log.getDescription()))
                .toList();
    }
}
