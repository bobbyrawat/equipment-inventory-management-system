package com.example.inventory.controller;

import com.example.inventory.dto.OperationDtos;
import com.example.inventory.service.InventoryOperationsService;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','BRANCH_MANAGER','LOGISTICS_OFFICER')")
public class PurchaseController {
    private final InventoryOperationsService operationsService;

    @PostMapping
    public ResponseEntity<OperationDtos.PurchaseResponse> create(@Valid @RequestBody OperationDtos.PurchaseRequest request) {
        OperationDtos.PurchaseResponse created = operationsService.createPurchase(request);
        return ResponseEntity.created(URI.create("/api/purchases/" + created.id())).body(created);
    }
    @GetMapping
    public List<OperationDtos.PurchaseResponse> getAll(@RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long equipmentId, @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return operationsService.purchases(branchId, equipmentId, from, to);
    }
    @GetMapping("/{id}")
    public OperationDtos.PurchaseResponse getById(@PathVariable Long id) { return operationsService.purchase(id); }
}
