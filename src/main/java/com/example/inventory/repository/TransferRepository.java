package com.example.inventory.repository;

import com.example.inventory.entity.Transfer;
import com.example.inventory.entity.TransferStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
	List<Transfer> findByFromBranch_IdOrToBranch_Id(Long fromBranchId, Long toBranchId);
	List<Transfer> findByStatus(TransferStatus status);
	List<Transfer> findByEquipment_IdAndFromBranch_IdAndStatus(Long equipmentId, Long branchId, TransferStatus status);
	List<Transfer> findByEquipment_IdAndToBranch_IdAndStatus(Long equipmentId, Long branchId, TransferStatus status);
	List<Transfer> findByDestinationEquipment_IdAndStatus(Long equipmentId, TransferStatus status);
}
