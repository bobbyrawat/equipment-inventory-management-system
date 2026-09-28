package com.example.inventory.dto;

import com.example.inventory.entity.EquipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public final class EquipmentDtos {
    private EquipmentDtos() { }
    public record Request(@NotBlank @Size(max = 160) String name,
                          @NotBlank @Size(max = 100) String category,
                          @Size(max = 2000) String description,
                          @NotNull @PositiveOrZero Integer quantity,
                          @NotBlank @Size(max = 40) String unit,
                          @NotNull EquipmentStatus status,
                          @NotNull @Positive Long branchId) { }
    public record Response(Long id, String name, String category, String description, Integer quantity,
                           String unit, EquipmentStatus status, Long branchId, String branchName,
                           java.time.Instant createdAt, java.time.Instant updatedAt) { }
}
