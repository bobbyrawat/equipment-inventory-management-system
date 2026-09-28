package com.example.inventory.dto;

import java.time.Instant;

public final class AuditDtos {
    private AuditDtos() { }
    public record Response(Long id, Long userId, String username, String action, String entityType,
                           Long entityId, Instant timestamp, String description) { }
}
