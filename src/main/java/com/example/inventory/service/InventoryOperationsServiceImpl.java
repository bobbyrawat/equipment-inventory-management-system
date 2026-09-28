package com.example.inventory.service;

import com.example.inventory.dto.OperationDtos;
import com.example.inventory.entity.Assignment;
import com.example.inventory.entity.Branch;
import com.example.inventory.entity.Equipment;
import com.example.inventory.entity.Expenditure;
import com.example.inventory.entity.Purchase;
import com.example.inventory.entity.Transfer;
import com.example.inventory.entity.TransferStatus;
import com.example.inventory.entity.User;
import com.example.inventory.exception.BadRequestException;
import com.example.inventory.exception.InsufficientInventoryException;
import com.example.inventory.exception.ResourceNotFoundException;
import com.example.inventory.repository.AssignmentRepository;
import com.example.inventory.repository.BranchRepository;
import com.example.inventory.repository.EquipmentRepository;
import com.example.inventory.repository.ExpenditureRepository;
import com.example.inventory.repository.PurchaseRepository;
import com.example.inventory.repository.TransferRepository;
import com.example.inventory.repository.UserRepository;
import com.example.inventory.security.CurrentUser;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryOperationsServiceImpl implements InventoryOperationsService {
    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExpenditureRepository expenditureRepository;
    private final EquipmentRepository equipmentRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;
    private final AuditService auditService;

    @Override
    public OperationDtos.PurchaseResponse createPurchase(OperationDtos.PurchaseRequest request) {
        currentUser.checkBranch(request.branchId());
        Equipment equipment = lockEquipment(request.equipmentId());
        requireEquipmentBranch(equipment, request.branchId());
        equipment.setQuantity(Math.addExact(equipment.getQuantity(), request.quantity()));
        Purchase purchase = new Purchase();
        purchase.setEquipment(equipment);
        purchase.setBranch(equipment.getBranch());
        purchase.setQuantity(request.quantity());
        purchase.setPurchaseDate(request.purchaseDate());
        purchase.setSupplier(request.supplier());
        purchase.setCost(request.cost());
        purchase.setRemarks(request.remarks());
        purchase.setCreatedBy(actor());
        purchase = purchaseRepository.save(purchase);
        audit("CREATE", "Purchase", purchase.getId(), "Purchase recorded for equipment " + equipment.getId());
        return purchaseResponse(purchase);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OperationDtos.PurchaseResponse> purchases(Long branchId, Long equipmentId, LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) throw new BadRequestException("from date must be on or before to date");
        if (branchId != null) currentUser.checkBranch(branchId);
        Long scopedBranchId = currentUser.isAdmin() ? branchId : currentUser.require().branchId();
        if (branchId != null && !currentUser.isAdmin()) currentUser.checkBranch(branchId);
        List<Purchase> found = purchaseRepository.search(scopedBranchId, equipmentId, from, to);
        return found.stream().map(this::purchaseResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OperationDtos.PurchaseResponse purchase(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase " + id + " was not found"));
        currentUser.checkBranch(purchase.getBranch().getId());
        return purchaseResponse(purchase);
    }

    @Override
    public OperationDtos.TransferResponse createTransfer(OperationDtos.TransferRequest request) {
        if (request.fromBranchId().equals(request.toBranchId())) throw new BadRequestException("Source and destination branches must differ");
        currentUser.checkBranch(request.fromBranchId());
        Equipment source = lockEquipment(request.equipmentId());
        requireEquipmentBranch(source, request.fromBranchId());
        Branch destination = branch(request.toBranchId());
        if (source.getQuantity() < request.quantity()) {
            throw new InsufficientInventoryException("Source branch does not have enough equipment for this transfer");
        }
        Transfer transfer = new Transfer();
        transfer.setEquipment(source);
        transfer.setFromBranch(source.getBranch());
        transfer.setToBranch(destination);
        transfer.setQuantity(request.quantity());
        transfer.setTransferDate(request.transferDate());
        transfer.setStatus(TransferStatus.PENDING);
        transfer.setRemarks(request.remarks());
        transfer.setCreatedBy(actor());
        transfer = transferRepository.save(transfer);
        audit("CREATE", "Transfer", transfer.getId(), "Transfer request created from branch " +
                request.fromBranchId() + " to branch " + request.toBranchId());
        return transferResponse(transfer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OperationDtos.TransferResponse> transfers() {
        List<Transfer> found = currentUser.isAdmin() ? transferRepository.findAll()
                : transferRepository.findByFromBranch_IdOrToBranch_Id(currentUser.require().branchId(), currentUser.require().branchId());
        return found.stream().map(this::transferResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OperationDtos.TransferResponse transfer(Long id) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer " + id + " was not found"));
        checkTransferAccess(transfer);
        return transferResponse(transfer);
    }

    @Override
    public OperationDtos.TransferResponse updateTransferStatus(Long id, TransferStatus status) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer " + id + " was not found"));
        checkTransferAccess(transfer);
        if (transfer.getStatus() == TransferStatus.COMPLETED || transfer.getStatus() == TransferStatus.REJECTED) {
            throw new BadRequestException("A completed or rejected transfer cannot be changed");
        }
        if (status == TransferStatus.PENDING) throw new BadRequestException("Transfer status cannot be reset to PENDING");
        if (status == TransferStatus.COMPLETED) completeTransfer(transfer);
        transfer.setStatus(status);
        audit("UPDATE", "Transfer", transfer.getId(), "Transfer status changed to " + status);
        return transferResponse(transfer);
    }

    private void completeTransfer(Transfer transfer) {
        Equipment source = lockEquipment(transfer.getEquipment().getId());
        if (source.getQuantity() < transfer.getQuantity()) {
            throw new InsufficientInventoryException("Source branch no longer has enough inventory to complete this transfer");
        }
        Equipment destination = equipmentRepository.findFirstByBranch_IdAndNameIgnoreCaseAndCategoryIgnoreCaseAndUnitIgnoreCase(
                        transfer.getToBranch().getId(), source.getName(), source.getCategory(), source.getUnit())
                .orElseGet(() -> createDestinationEquipment(source, transfer.getToBranch()));
        destination = equipmentRepository.findByIdForUpdate(destination.getId()).orElse(destination);
        source.setQuantity(source.getQuantity() - transfer.getQuantity());
        destination.setQuantity(Math.addExact(destination.getQuantity(), transfer.getQuantity()));
        equipmentRepository.save(source);
        equipmentRepository.save(destination);
        transfer.setDestinationEquipment(destination);
    }

    private Equipment createDestinationEquipment(Equipment source, Branch destinationBranch) {
        Equipment destination = new Equipment();
        destination.setName(source.getName());
        destination.setCategory(source.getCategory());
        destination.setDescription(source.getDescription());
        destination.setUnit(source.getUnit());
        destination.setStatus(source.getStatus());
        destination.setQuantity(0);
        destination.setBranch(destinationBranch);
        return equipmentRepository.save(destination);
    }

    @Override
    public OperationDtos.AssignmentResponse createAssignment(OperationDtos.AssignmentRequest request) {
        currentUser.checkBranch(request.branchId());
        Equipment equipment = lockEquipment(request.equipmentId());
        requireEquipmentBranch(equipment, request.branchId());
        User assignedTo = userRepository.findById(request.assignedToId())
                .orElseThrow(() -> new ResourceNotFoundException("Assigned user " + request.assignedToId() + " was not found"));
        if (assignedTo.getBranch() == null || !assignedTo.getBranch().getId().equals(request.branchId())) {
            throw new BadRequestException("The assigned user must belong to the equipment branch");
        }
        requireAvailable(equipment, request.quantity());
        equipment.setQuantity(equipment.getQuantity() - request.quantity());
        Assignment assignment = new Assignment();
        assignment.setEquipment(equipment);
        assignment.setBranch(equipment.getBranch());
        assignment.setAssignedTo(assignedTo);
        assignment.setQuantity(request.quantity());
        assignment.setAssignmentDate(request.assignmentDate());
        assignment.setRemarks(request.remarks());
        assignment.setCreatedBy(actor());
        assignment = assignmentRepository.save(assignment);
        audit("CREATE", "Assignment", assignment.getId(), "Equipment assigned to user " + assignedTo.getId());
        return assignmentResponse(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OperationDtos.AssignmentResponse> assignments() {
        List<Assignment> found = currentUser.isAdmin() ? assignmentRepository.findAll()
                : assignmentRepository.findByBranch_Id(currentUser.require().branchId());
        return found.stream().map(this::assignmentResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OperationDtos.AssignmentResponse assignment(Long id) {
        Assignment item = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment " + id + " was not found"));
        currentUser.checkBranch(item.getBranch().getId());
        return assignmentResponse(item);
    }

    @Override
    public OperationDtos.ExpenditureResponse createExpenditure(OperationDtos.ExpenditureRequest request) {
        currentUser.checkBranch(request.branchId());
        Equipment equipment = lockEquipment(request.equipmentId());
        requireEquipmentBranch(equipment, request.branchId());
        requireAvailable(equipment, request.quantity());
        equipment.setQuantity(equipment.getQuantity() - request.quantity());
        Expenditure expenditure = new Expenditure();
        expenditure.setEquipment(equipment);
        expenditure.setBranch(equipment.getBranch());
        expenditure.setQuantity(request.quantity());
        expenditure.setExpenditureDate(request.expenditureDate());
        expenditure.setReason(request.reason().trim());
        expenditure.setRemarks(request.remarks());
        expenditure.setCreatedBy(actor());
        expenditure = expenditureRepository.save(expenditure);
        audit("CREATE", "Expenditure", expenditure.getId(), "Expenditure recorded: " + expenditure.getReason());
        return expenditureResponse(expenditure);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OperationDtos.ExpenditureResponse> expenditures() {
        List<Expenditure> found = currentUser.isAdmin() ? expenditureRepository.findAll()
                : expenditureRepository.findByBranch_Id(currentUser.require().branchId());
        return found.stream().map(this::expenditureResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OperationDtos.ExpenditureResponse expenditure(Long id) {
        Expenditure item = expenditureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expenditure " + id + " was not found"));
        currentUser.checkBranch(item.getBranch().getId());
        return expenditureResponse(item);
    }

    private void requireAvailable(Equipment equipment, int quantity) {
        if (equipment.getQuantity() < quantity) {
            throw new InsufficientInventoryException("Available equipment quantity is insufficient");
        }
    }
    private void requireEquipmentBranch(Equipment equipment, Long branchId) {
        if (!equipment.getBranch().getId().equals(branchId)) {
            throw new BadRequestException("Equipment does not belong to the specified branch");
        }
    }
    private Equipment lockEquipment(Long id) {
        return equipmentRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment " + id + " was not found"));
    }
    private Branch branch(Long id) {
        return branchRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Branch " + id + " was not found"));
    }
    private User actor() { return userRepository.getReferenceById(currentUser.require().id()); }
    private void checkTransferAccess(Transfer transfer) {
        if (!currentUser.isAdmin()) {
            Long userBranch = currentUser.require().branchId();
            if (!transfer.getFromBranch().getId().equals(userBranch)
                    && !transfer.getToBranch().getId().equals(userBranch)) {
                throw new ResourceNotFoundException("Transfer was not found");
            }
        }
    }
    private void audit(String action, String type, Long id, String description) {
        auditService.record(actor(), action, type, id, description);
    }
    private OperationDtos.PurchaseResponse purchaseResponse(Purchase p) {
        return new OperationDtos.PurchaseResponse(p.getId(), p.getEquipment().getId(), p.getBranch().getId(), p.getQuantity(),
                p.getPurchaseDate(), p.getSupplier(), p.getCost(), p.getRemarks(), p.getCreatedBy().getId());
    }
    private OperationDtos.TransferResponse transferResponse(Transfer t) {
        return new OperationDtos.TransferResponse(t.getId(), t.getEquipment().getId(), t.getFromBranch().getId(),
                t.getToBranch().getId(), t.getQuantity(), t.getTransferDate(), t.getStatus(), t.getRemarks(), t.getCreatedBy().getId());
    }
    private OperationDtos.AssignmentResponse assignmentResponse(Assignment a) {
        return new OperationDtos.AssignmentResponse(a.getId(), a.getEquipment().getId(), a.getBranch().getId(),
                a.getAssignedTo().getId(), a.getQuantity(), a.getAssignmentDate(), a.getRemarks(), a.getCreatedBy().getId());
    }
    private OperationDtos.ExpenditureResponse expenditureResponse(Expenditure e) {
        return new OperationDtos.ExpenditureResponse(e.getId(), e.getEquipment().getId(), e.getBranch().getId(),
                e.getQuantity(), e.getExpenditureDate(), e.getReason(), e.getRemarks(), e.getCreatedBy().getId());
    }
}
