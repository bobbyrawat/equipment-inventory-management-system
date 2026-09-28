package com.example.inventory.repository;

import com.example.inventory.entity.Purchase;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
	List<Purchase> findByBranch_Id(Long branchId);
	List<Purchase> findByBranch_IdAndEquipment_IdAndPurchaseDateBetween(Long branchId, Long equipmentId,
			LocalDate from, LocalDate to);
	List<Purchase> findByEquipment_IdAndPurchaseDateBetween(Long equipmentId, LocalDate from, LocalDate to);
	List<Purchase> findByBranch_IdAndPurchaseDateBetween(Long branchId, LocalDate from, LocalDate to);
	List<Purchase> findByPurchaseDateBetween(LocalDate from, LocalDate to);

	    @Query("select p from Purchase p where (:branchId is null or p.branch.id = :branchId) "
		    + "and (:equipmentId is null or p.equipment.id = :equipmentId) "
		    + "and (:fromDate is null or p.purchaseDate >= :fromDate) "
		    + "and (:toDate is null or p.purchaseDate <= :toDate)")
	    List<Purchase> search(@Param("branchId") Long branchId, @Param("equipmentId") Long equipmentId,
		    @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

	@Query("select coalesce(sum(p.quantity), 0) from Purchase p where p.equipment.id = :equipmentId and p.branch.id = :branchId")
	Long sumQuantityForEquipmentAndBranch(@Param("equipmentId") Long equipmentId, @Param("branchId") Long branchId);
}
