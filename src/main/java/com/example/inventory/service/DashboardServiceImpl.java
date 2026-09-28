package com.example.inventory.service;

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
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        List<Equipment> equipment = currentUser.isAdmin() ? equipmentRepository.findAll()
                : equipmentRepository.findByBranch_Id(currentUser.require().branchId());
        List<InventoryDtos.Balance> balances = equipment.stream().map(this::balance).toList();
        long branches = currentUser.isAdmin() ? branchRepository.count() : 1;
        long pending = currentUser.isAdmin() ? transferRepository.findByStatus(TransferStatus.PENDING).size()
                : transferRepository.findByFromBranch_IdOrToBranch_Id(currentUser.require().branchId(),
                        currentUser.require().branchId()).stream().filter(t -> t.getStatus() == TransferStatus.PENDING).count();
        long units = balances.stream().mapToLong(InventoryDtos.Balance::closingBalance).sum();
        long lowStock = balances.stream().filter(balance -> balance.closingBalance() <= lowStockThreshold).count();
        return new InventoryDtos.Dashboard(LocalDate.now(), equipment.size(), branches, lowStock, pending, units, balances);
    }

    private InventoryDtos.Balance balance(Equipment item) {
        Long equipmentId = item.getId();
        Long branchId = item.getBranch().getId();
        int purchases = Math.toIntExact(purchaseRepository.sumQuantityForEquipmentAndBranch(equipmentId, branchId));
        int transferOut = transferRepository.findByEquipment_IdAndFromBranch_IdAndStatus(
                        equipmentId, branchId, TransferStatus.COMPLETED)
                .stream().mapToInt(transfer -> transfer.getQuantity()).sum();
        int transferIn = transferRepository.findByDestinationEquipment_IdAndStatus(equipmentId, TransferStatus.COMPLETED)
                .stream().mapToInt(transfer -> transfer.getQuantity()).sum();
        int assigned = assignmentRepository.findByEquipment_IdAndBranch_Id(equipmentId, branchId)
                .stream().mapToInt(assignment -> assignment.getQuantity()).sum();
        int expended = expenditureRepository.findByEquipment_IdAndBranch_Id(equipmentId, branchId)
                .stream().mapToInt(expenditure -> expenditure.getQuantity()).sum();
        int closing = item.getQuantity();
        int opening = closing - purchases - transferIn + transferOut + assigned + expended;
        return new InventoryDtos.Balance(equipmentId, item.getName(), item.getCategory(), branchId,
                item.getBranch().getName(), opening, purchases, transferIn, transferOut, assigned, expended, closing);
    }
}
