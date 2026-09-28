package com.example.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class OperationDtos {
    private OperationDtos() { }

        public record PurchaseRequest(@NotNull @Positive Long equipmentId, @NotNull @Positive Long branchId,
            @NotNull @Positive Integer quantity, @NotNull LocalDate purchaseDate,
            @Size(max = 160) String supplier, @DecimalMin("0.00") BigDecimal cost, @Size(max = 2000) String remarks) { }
    public record PurchaseResponse(Long id, Long equipmentId, Long branchId, Integer quantity,
            LocalDate purchaseDate, String supplier, BigDecimal cost, String remarks, Long createdById) { }

    public record TransferRequest(@NotNull @Positive Long equipmentId, @NotNull @Positive Long fromBranchId,
            @NotNull @Positive Long toBranchId,
            @NotNull @Positive Integer quantity, @NotNull LocalDate transferDate, @Size(max = 2000) String remarks) { }
    public record TransferResponse(Long id, Long equipmentId, Long fromBranchId, Long toBranchId, Integer quantity,
            LocalDate transferDate, com.example.inventory.entity.TransferStatus status, String remarks, Long createdById) { }

    public record AssignmentRequest(@NotNull @Positive Long equipmentId, @NotNull @Positive Long branchId,
            @NotNull @Positive Long assignedToId,
            @NotNull @Positive Integer quantity, @NotNull LocalDate assignmentDate, @Size(max = 2000) String remarks) { }
    public record AssignmentResponse(Long id, Long equipmentId, Long branchId, Long assignedToId, Integer quantity,
            LocalDate assignmentDate, String remarks, Long createdById) { }

        public record ExpenditureRequest(@NotNull @Positive Long equipmentId, @NotNull @Positive Long branchId,
            @NotNull @Positive Integer quantity, @NotNull LocalDate expenditureDate,
            @NotBlank @Size(max = 500) String reason, @Size(max = 2000) String remarks) { }
    public record ExpenditureResponse(Long id, Long equipmentId, Long branchId, Integer quantity,
            LocalDate expenditureDate, String reason, String remarks, Long createdById) { }

    public record TransferStatusRequest(@NotNull com.example.inventory.entity.TransferStatus status) { }
}
