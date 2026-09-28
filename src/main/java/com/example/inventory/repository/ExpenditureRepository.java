package com.example.inventory.repository;

import com.example.inventory.entity.Expenditure;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {
	List<Expenditure> findByBranch_Id(Long branchId);
	List<Expenditure> findByEquipment_IdAndBranch_Id(Long equipmentId, Long branchId);
}
