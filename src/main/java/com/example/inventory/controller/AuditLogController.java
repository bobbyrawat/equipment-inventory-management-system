package com.example.inventory.controller;

import com.example.inventory.dto.AuditDtos;
import com.example.inventory.service.AuditService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {
    private final AuditService auditService;

    @GetMapping
    public List<AuditDtos.Response> getAll() { return auditService.getAll(); }
}
