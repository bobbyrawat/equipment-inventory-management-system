package com.example.inventory.service;

import com.example.inventory.dto.EquipmentDtos;
import com.example.inventory.entity.Branch;
import com.example.inventory.entity.Equipment;
import com.example.inventory.exception.ResourceNotFoundException;
import com.example.inventory.repository.BranchRepository;
import com.example.inventory.repository.EquipmentRepository;
import com.example.inventory.repository.UserRepository;
import com.example.inventory.security.CurrentUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipmentServiceImpl implements EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;
    private final AuditService auditService;

    @Override
    public EquipmentDtos.Response create(EquipmentDtos.Request request) {
        currentUser.checkBranch(request.branchId());
        Equipment equipment = new Equipment();
        apply(equipment, request);
        equipment = equipmentRepository.save(equipment);
        audit("CREATE", equipment, "Equipment created: " + equipment.getName());
        return response(equipment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentDtos.Response> getAll() {
        List<Equipment> equipment = currentUser.isAdmin() ? equipmentRepository.findAll()
                : equipmentRepository.findByBranch_Id(currentUser.require().branchId());
        return equipment.stream().map(this::response).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentDtos.Response getById(Long id) { return response(find(id)); }

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentDtos.Response> search(String query) {
        String term = query == null ? "" : query.trim();
        List<Equipment> results;
        if (currentUser.isAdmin()) {
            results = equipmentRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(term, term);
        } else {
            Long branchId = currentUser.require().branchId();
            results = equipmentRepository.findByBranch_IdAndNameContainingIgnoreCaseOrBranch_IdAndCategoryContainingIgnoreCase(
                    branchId, term, branchId, term);
        }
        return results.stream().map(this::response).toList();
    }

    @Override
    public EquipmentDtos.Response update(Long id, EquipmentDtos.Request request) {
        Equipment equipment = find(id);
        currentUser.checkBranch(request.branchId());
        if (!equipment.getQuantity().equals(request.quantity())) {
            throw new com.example.inventory.exception.BadRequestException(
                    "Quantity changes must use the purchase, transfer, assignment, or expenditure endpoints");
        }
        if (!equipment.getBranch().getId().equals(request.branchId())) {
            throw new com.example.inventory.exception.BadRequestException(
                    "Equipment cannot be moved between branches; create a transfer instead");
        }
        apply(equipment, request);
        audit("UPDATE", equipment, "Equipment updated: " + equipment.getName());
        return response(equipment);
    }

    @Override
    public void delete(Long id) {
        Equipment equipment = find(id);
        audit("DELETE", equipment, "Equipment deleted: " + equipment.getName());
        equipmentRepository.delete(equipment);
    }

    private Equipment find(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment " + id + " was not found"));
        currentUser.checkBranch(equipment.getBranch().getId());
        return equipment;
    }

    private void apply(Equipment equipment, EquipmentDtos.Request request) {
        Branch branch = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch " + request.branchId() + " was not found"));
        equipment.setName(request.name().trim());
        equipment.setCategory(request.category().trim());
        equipment.setDescription(request.description());
        equipment.setQuantity(request.quantity());
        equipment.setUnit(request.unit().trim());
        equipment.setStatus(request.status());
        equipment.setBranch(branch);
    }

    private EquipmentDtos.Response response(Equipment equipment) {
        return new EquipmentDtos.Response(equipment.getId(), equipment.getName(), equipment.getCategory(),
                equipment.getDescription(), equipment.getQuantity(), equipment.getUnit(), equipment.getStatus(),
                equipment.getBranch().getId(), equipment.getBranch().getName(), equipment.getCreatedAt(), equipment.getUpdatedAt());
    }

    private void audit(String action, Equipment equipment, String description) {
        auditService.record(userRepository.getReferenceById(currentUser.require().id()), action,
                "Equipment", equipment.getId(), description);
    }
}
