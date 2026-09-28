package com.example.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.inventory.dto.OperationDtos;
import com.example.inventory.entity.Branch;
import com.example.inventory.entity.Equipment;
import com.example.inventory.entity.EquipmentStatus;
import com.example.inventory.entity.Role;
import com.example.inventory.entity.User;
import com.example.inventory.exception.InsufficientInventoryException;
import com.example.inventory.repository.AssignmentRepository;
import com.example.inventory.repository.BranchRepository;
import com.example.inventory.repository.EquipmentRepository;
import com.example.inventory.repository.ExpenditureRepository;
import com.example.inventory.repository.PurchaseRepository;
import com.example.inventory.repository.TransferRepository;
import com.example.inventory.repository.UserRepository;
import com.example.inventory.security.CurrentUser;
import com.example.inventory.security.InventoryPrincipal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryOperationsServiceImplTest {
    @Mock private PurchaseRepository purchaseRepository;
    @Mock private TransferRepository transferRepository;
    @Mock private AssignmentRepository assignmentRepository;
    @Mock private ExpenditureRepository expenditureRepository;
    @Mock private EquipmentRepository equipmentRepository;
    @Mock private BranchRepository branchRepository;
    @Mock private UserRepository userRepository;
    @Mock private CurrentUser currentUser;
    @Mock private AuditService auditService;
    @InjectMocks private InventoryOperationsServiceImpl service;

    private Equipment equipment;
    private User actor;

    @BeforeEach
    void setUp() {
        Branch branch = new Branch();
        branch.setId(1L);
        branch.setName("Central");
        equipment = new Equipment();
        equipment.setId(5L);
        equipment.setName("Radio");
        equipment.setCategory("Communications");
        equipment.setUnit("each");
        equipment.setQuantity(8);
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        equipment.setBranch(branch);
        actor = new User();
        actor.setId(20L);
    }

    @Test
    void purchaseAddsQuantityToBranchInventory() {
        when(currentUser.require()).thenReturn(new InventoryPrincipal(20L, "operator", "hash", Role.LOGISTICS_OFFICER, 1L));
        when(userRepository.getReferenceById(20L)).thenReturn(actor);
        when(equipmentRepository.findByIdForUpdate(5L)).thenReturn(Optional.of(equipment));
        when(purchaseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        OperationDtos.PurchaseRequest request = new OperationDtos.PurchaseRequest(
                5L, 1L, 4, LocalDate.of(2026, 9, 1), "Supplier", null, "Initial stock");

        OperationDtos.PurchaseResponse response = service.createPurchase(request);

        assertEquals(12, equipment.getQuantity());
        assertEquals(4, response.quantity());
        verify(auditService).record(actor, "CREATE", "Purchase", null,
                "Purchase recorded for equipment 5");
    }

    @Test
    void expenditureIsRejectedWhenStockIsInsufficient() {
        equipment.setQuantity(2);
        when(equipmentRepository.findByIdForUpdate(5L)).thenReturn(Optional.of(equipment));
        OperationDtos.ExpenditureRequest request = new OperationDtos.ExpenditureRequest(
                5L, 1L, 3, LocalDate.of(2026, 9, 1), "Damaged", null);

        assertThrows(InsufficientInventoryException.class, () -> service.createExpenditure(request));

        assertEquals(2, equipment.getQuantity());
        verify(expenditureRepository, never()).save(any());
    }
}
