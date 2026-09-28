package com.example.inventory.controller;

import com.example.inventory.dto.OperationDtos;
import com.example.inventory.service.InventoryOperationsService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','BRANCH_MANAGER','LOGISTICS_OFFICER')")
public class TransferController {
    private final InventoryOperationsService operationsService;

    @PostMapping
    public ResponseEntity<OperationDtos.TransferResponse> create(@Valid @RequestBody OperationDtos.TransferRequest request) {
        OperationDtos.TransferResponse created = operationsService.createTransfer(request);
        return ResponseEntity.created(URI.create("/api/transfers/" + created.id())).body(created);
    }
    @GetMapping
    public List<OperationDtos.TransferResponse> getAll() { return operationsService.transfers(); }
    @GetMapping("/{id}")
    public OperationDtos.TransferResponse getById(@PathVariable Long id) { return operationsService.transfer(id); }
    @PatchMapping("/{id}/status")
    public OperationDtos.TransferResponse updateStatus(@PathVariable Long id,
            @Valid @RequestBody OperationDtos.TransferStatusRequest request) {
        return operationsService.updateTransferStatus(id, request.status());
    }
}
