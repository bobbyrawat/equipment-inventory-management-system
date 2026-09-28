package com.example.inventory.dto;

import java.time.LocalDate;

public final class InventoryDtos {
    private InventoryDtos() { }
    public record Balance(Long equipmentId, String equipmentName, String category, Long branchId, String branchName,
                         int openingBalance, int purchases, int transferIn, int transferOut,
                         int assigned, int expended, int closingBalance) { }
    public record Dashboard(LocalDate asOfDate, long equipmentTypes, long branches, long lowStockItems,
                            long pendingTransfers, long totalUnits, java.util.List<Balance> inventory) { }
}
