package com.example.inventory.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.inventory.dto.InventoryDtos;
import com.example.inventory.entity.Equipment;
import com.example.inventory.entity.TransferStatus;
import com.example.inventory.repository.AssignmentRepository;
import com.example.inventory.repository.BranchRepository;
import com.example.inventory.repository.EquipmentRepository;
import com.example.inventory.repository.ExpenditureRepository;
import com.example.inventory.repository.PurchaseRepository;
import com.example.inventory.repository.TransferRepository;
import com.example.inventory.security.CurrentUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final EquipmentRepository equipmentRepository;
    private final BranchRepository branchRepository;
    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExpenditureRepository expenditureRepository;
    private final CurrentUser currentUser;

    @Value("${app.inventory.low-stock-threshold:5}")
    private int lowStockThreshold;

    @Override
    public InventoryDtos.Dashboard dashboard() {

        boolean admin = currentUser.isAdmin();

        Long branchId = admin
                ? null
                : currentUser.require().branchId();

        List<Equipment> equipment = admin
                ? equipmentRepository.findAll()
                : equipmentRepository.findByBranch_Id(branchId);

        Map<Long, Integer> purchases = new HashMap<>();
        Map<Long, Integer> transferOut = new HashMap<>();
        Map<Long, Integer> transferIn = new HashMap<>();
        Map<Long, Integer> assigned = new HashMap<>();
        Map<Long, Integer> expended = new HashMap<>();

        if (admin) {

            // Purchases for all equipment
            for (Object[] row : purchaseRepository.sumQuantityByEquipment()) {
                purchases.put(
                        (Long) row[0],
                        ((Number) row[1]).intValue()
                );
            }

            // Transfer OUT for all equipment
            for (Object[] row : transferRepository
                    .sumTransferOutByEquipment(TransferStatus.COMPLETED)) {

                transferOut.put(
                        (Long) row[0],
                        ((Number) row[1]).intValue()
                );
            }

            // Transfer IN for all equipment
            for (Object[] row : transferRepository
                    .sumTransferInByEquipment(TransferStatus.COMPLETED)) {

                transferIn.put(
                        (Long) row[0],
                        ((Number) row[1]).intValue()
                );
            }

            // Assigned quantities for all equipment
            for (Object[] row : assignmentRepository
                    .sumQuantityByEquipment()) {

                assigned.put(
                        (Long) row[0],
                        ((Number) row[1]).intValue()
                );
            }

            // Expended quantities for all equipment
            for (Object[] row : expenditureRepository
                    .sumQuantityByEquipment()) {

                expended.put(
                        (Long) row[0],
                        ((Number) row[1]).intValue()
                );
            }

        } else {

            // Purchases for current branch
            for (Object[] row : purchaseRepository
                    .sumQuantityByEquipmentAndBranch(branchId)) {

                purchases.put(
                        (Long) row[0],
                        ((Number) row[1]).intValue()
                );
            }

            // Transfer OUT for current branch
            for (Object[] row : transferRepository
                    .sumTransferOutByEquipmentAndBranch(
                            branchId,
                            TransferStatus.COMPLETED)) {

                transferOut.put(
                        (Long) row[0],
                        ((Number) row[1]).intValue()
                );
            }

            // Transfer IN
            for (Object[] row : transferRepository
                    .sumTransferInByEquipment(TransferStatus.COMPLETED)) {

                transferIn.put(
                        (Long) row[0],
                        ((Number) row[1]).intValue()
                );
            }

            // Assigned quantities for current branch
            for (Object[] row : assignmentRepository
                    .sumQuantityByEquipmentAndBranch(branchId)) {

                assigned.put(
                        (Long) row[0],
                        ((Number) row[1]).intValue()
                );
            }

            // Expended quantities for current branch
            for (Object[] row : expenditureRepository
                    .sumQuantityByEquipmentAndBranch(branchId)) {

                expended.put(
                        (Long) row[0],
                        ((Number) row[1]).intValue()
                );
            }
        }

        List<InventoryDtos.Balance> balances = equipment.stream()
                .map(item -> balance(
                        item,
                        purchases,
                        transferIn,
                        transferOut,
                        assigned,
                        expended
                ))
                .toList();

        long branches = admin
                ? branchRepository.count()
                : 1;

        long pending = admin
                ? transferRepository
                        .findByStatus(TransferStatus.PENDING)
                        .size()
                : transferRepository
                        .findByFromBranch_IdOrToBranch_Id(
                                branchId,
                                branchId
                        )
                        .stream()
                        .filter(t -> t.getStatus() == TransferStatus.PENDING)
                        .count();

        long units = balances.stream()
                .mapToLong(InventoryDtos.Balance::closingBalance)
                .sum();

        long lowStock = balances.stream()
                .filter(balance ->
                        balance.closingBalance() <= lowStockThreshold
                )
                .count();

        return new InventoryDtos.Dashboard(
                LocalDate.now(),
                equipment.size(),
                branches,
                lowStock,
                pending,
                units,
                balances
        );
    }

    private InventoryDtos.Balance balance(
            Equipment item,
            Map<Long, Integer> purchases,
            Map<Long, Integer> transferIn,
            Map<Long, Integer> transferOut,
            Map<Long, Integer> assigned,
            Map<Long, Integer> expended
    ) {

        Long equipmentId = item.getId();

        int purchaseQty =
                purchases.getOrDefault(equipmentId, 0);

        int transferOutQty =
                transferOut.getOrDefault(equipmentId, 0);

        int transferInQty =
                transferIn.getOrDefault(equipmentId, 0);

        int assignedQty =
                assigned.getOrDefault(equipmentId, 0);

        int expendedQty =
                expended.getOrDefault(equipmentId, 0);

        int closing = item.getQuantity();

        int opening =
                closing
                - purchaseQty
                - transferInQty
                + transferOutQty
                + assignedQty
                + expendedQty;

        return new InventoryDtos.Balance(
                equipmentId,
                item.getName(),
                item.getCategory(),
                item.getBranch().getId(),
                item.getBranch().getName(),
                opening,
                purchaseQty,
                transferInQty,
                transferOutQty,
                assignedQty,
                expendedQty,
                closing
        );
    }
}