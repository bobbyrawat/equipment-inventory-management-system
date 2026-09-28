package com.example.inventory;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.inventory.repository.AssignmentRepository;
import com.example.inventory.repository.AuditLogRepository;
import com.example.inventory.repository.BranchRepository;
import com.example.inventory.repository.EquipmentRepository;
import com.example.inventory.repository.ExpenditureRepository;
import com.example.inventory.repository.PurchaseRepository;
import com.example.inventory.repository.TransferRepository;
import com.example.inventory.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:inventory;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "app.jwt.secret=01234567890123456789012345678901"
})
class InventoryApplicationContextTest {
    @Autowired private UserRepository userRepository;
    @Autowired private BranchRepository branchRepository;
    @Autowired private EquipmentRepository equipmentRepository;
    @Autowired private PurchaseRepository purchaseRepository;
    @Autowired private TransferRepository transferRepository;
    @Autowired private AssignmentRepository assignmentRepository;
    @Autowired private ExpenditureRepository expenditureRepository;
    @Autowired private AuditLogRepository auditLogRepository;

    @Test
    void contextLoadsWithAllRepositoriesAndEntityMappings() {
        assertThat(userRepository).isNotNull();
        assertThat(branchRepository).isNotNull();
        assertThat(equipmentRepository).isNotNull();
        assertThat(purchaseRepository).isNotNull();
        assertThat(transferRepository).isNotNull();
        assertThat(assignmentRepository).isNotNull();
        assertThat(expenditureRepository).isNotNull();
        assertThat(auditLogRepository).isNotNull();
    }
}