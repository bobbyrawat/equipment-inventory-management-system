package com.example.inventory.repository;

import com.example.inventory.entity.Assignment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
	List<Assignment> findByBranch_Id(Long branchId);
	List<Assignment> findByEquipment_IdAndBranch_Id(Long equipmentId, Long branchId);
}
