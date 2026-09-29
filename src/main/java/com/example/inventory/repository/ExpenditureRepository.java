package com.example.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.inventory.entity.Expenditure;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    List<Expenditure> findByBranch_Id(Long branchId);

    List<Expenditure> findByEquipment_IdAndBranch_Id(
            Long equipmentId,
            Long branchId
    );

    @Query("""
        select e.equipment.id, coalesce(sum(e.quantity), 0)
        from Expenditure e
        where e.branch.id = :branchId
        group by e.equipment.id
        """)
    List<Object[]> sumQuantityByEquipmentAndBranch(
            @Param("branchId") Long branchId
    );

    @Query("""
        select e.equipment.id, coalesce(sum(e.quantity), 0)
        from Expenditure e
        group by e.equipment.id
        """)
    List<Object[]> sumQuantityByEquipment();
}