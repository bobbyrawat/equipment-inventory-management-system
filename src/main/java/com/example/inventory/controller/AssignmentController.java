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
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','BRANCH_MANAGER','LOGISTICS_OFFICER')")
public class AssignmentController {
    private final InventoryOperationsService operationsService;

    @PostMapping
    public ResponseEntity<OperationDtos.AssignmentResponse> create(@Valid @RequestBody OperationDtos.AssignmentRequest request) {
        OperationDtos.AssignmentResponse created = operationsService.createAssignment(request);
        return ResponseEntity.created(URI.create("/api/assignments/" + created.id())).body(created);
    }
    @GetMapping
    public List<OperationDtos.AssignmentResponse> getAll() { return operationsService.assignments(); }
    @GetMapping("/{id}")
    public OperationDtos.AssignmentResponse getById(@PathVariable Long id) { return operationsService.assignment(id); }
}
