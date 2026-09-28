package com.example.inventory.controller;

import com.example.inventory.dto.EquipmentDtos;
import com.example.inventory.service.EquipmentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','BRANCH_MANAGER','LOGISTICS_OFFICER')")
public class EquipmentController {
    private final EquipmentService equipmentService;

    @PostMapping
    public ResponseEntity<EquipmentDtos.Response> create(@Valid @RequestBody EquipmentDtos.Request request) {
        EquipmentDtos.Response created = equipmentService.create(request);
        return ResponseEntity.created(URI.create("/api/equipment/" + created.id())).body(created);
    }
    @GetMapping
    public List<EquipmentDtos.Response> getAll(@RequestParam(required = false) String q) {
        return q == null ? equipmentService.getAll() : equipmentService.search(q);
    }
    @GetMapping("/search")
    public List<EquipmentDtos.Response> search(@RequestParam String q) { return equipmentService.search(q); }
    @GetMapping("/{id}")
    public EquipmentDtos.Response getById(@PathVariable Long id) { return equipmentService.getById(id); }
    @PutMapping("/{id}")
    public EquipmentDtos.Response update(@PathVariable Long id, @Valid @RequestBody EquipmentDtos.Request request) {
        return equipmentService.update(id, request);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
