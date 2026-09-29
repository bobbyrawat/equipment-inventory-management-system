package com.example.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.inventory.entity.Assignment;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByBranch_Id(Long branchId);

    List<Assignment> findByEquipment_IdAndBranch_Id(
            Long equipmentId,
            Long branchId
    );

    @Query("""
        select a.equipment.id, coalesce(sum(a.quantity), 0)
        from Assignment a
        where a.branch.id = :branchId
        group by a.equipment.id
        """)
    List<Object[]> sumQuantityByEquipmentAndBranch(
            @Param("branchId") Long branchId
    );

    @Query("""
        select a.equipment.id, coalesce(sum(a.quantity), 0)
        from Assignment a
        group by a.equipment.id
        """)
    List<Object[]> sumQuantityByEquipment();
}