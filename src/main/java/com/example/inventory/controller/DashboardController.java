package com.example.inventory.controller;

import com.example.inventory.dto.InventoryDtos;
import com.example.inventory.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','BRANCH_MANAGER','LOGISTICS_OFFICER')")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public InventoryDtos.Dashboard getDashboard() { return dashboardService.dashboard(); }
}
