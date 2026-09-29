package com.example.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.inventory.entity.Transfer;
import com.example.inventory.entity.TransferStatus;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findByFromBranch_IdOrToBranch_Id(
            Long fromBranchId,
            Long toBranchId
    );

    List<Transfer> findByStatus(TransferStatus status);

    List<Transfer> findByEquipment_IdAndFromBranch_IdAndStatus(
            Long equipmentId,
            Long branchId,
            TransferStatus status
    );

    List<Transfer> findByEquipment_IdAndToBranch_IdAndStatus(
            Long equipmentId,
            Long branchId,
            TransferStatus status
    );

    List<Transfer> findByDestinationEquipment_IdAndStatus(
            Long equipmentId,
            TransferStatus status
    );

    // Transfer OUT for a specific branch
    @Query("""
        select t.equipment.id, coalesce(sum(t.quantity), 0)
        from Transfer t
        where t.fromBranch.id = :branchId
          and t.status = :status
        group by t.equipment.id
        """)
    List<Object[]> sumTransferOutByEquipmentAndBranch(
            @Param("branchId") Long branchId,
            @Param("status") TransferStatus status
    );

    // Transfer OUT for all branches - used by ADMIN dashboard
    @Query("""
        select t.equipment.id, coalesce(sum(t.quantity), 0)
        from Transfer t
        where t.status = :status
        group by t.equipment.id
        """)
    List<Object[]> sumTransferOutByEquipment(
            @Param("status") TransferStatus status
    );

    // Transfer IN based on destination equipment
    @Query("""
        select t.destinationEquipment.id, coalesce(sum(t.quantity), 0)
        from Transfer t
        where t.destinationEquipment.id is not null
          and t.status = :status
        group by t.destinationEquipment.id
        """)
    List<Object[]> sumTransferInByEquipment(
            @Param("status") TransferStatus status
    );
}