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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expenditures")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','BRANCH_MANAGER','LOGISTICS_OFFICER')")
public class ExpenditureController {
    private final InventoryOperationsService operationsService;

    @PostMapping
    public ResponseEntity<OperationDtos.ExpenditureResponse> create(@Valid @RequestBody OperationDtos.ExpenditureRequest request) {
        OperationDtos.ExpenditureResponse created = operationsService.createExpenditure(request);
        return ResponseEntity.created(URI.create("/api/expenditures/" + created.id())).body(created);
    }
    @GetMapping
    public List<OperationDtos.ExpenditureResponse> getAll() { return operationsService.expenditures(); }
    @GetMapping("/{id}")
    public OperationDtos.ExpenditureResponse getById(@PathVariable Long id) { return operationsService.expenditure(id); }
}
