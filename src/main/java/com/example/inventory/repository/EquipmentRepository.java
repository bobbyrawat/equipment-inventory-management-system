package com.example.inventory.repository;

import com.example.inventory.entity.Equipment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select e from Equipment e where e.id = :id")
	Optional<Equipment> findByIdForUpdate(Long id);

	List<Equipment> findByBranch_Id(Long branchId);
	Optional<Equipment> findFirstByBranch_IdAndNameIgnoreCaseAndCategoryIgnoreCaseAndUnitIgnoreCase(
			Long branchId, String name, String category, String unit);
	List<Equipment> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String name, String category);
	List<Equipment> findByBranch_IdAndNameContainingIgnoreCaseOrBranch_IdAndCategoryContainingIgnoreCase(
			Long branchId1, String name, Long branchId2, String category);
}
