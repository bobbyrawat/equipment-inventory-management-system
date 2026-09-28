package com.example.inventory.repository;

import com.example.inventory.entity.Branch;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
	Optional<Branch> findByNameIgnoreCase(String name);
}
