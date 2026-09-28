package com.example.inventory.service;

import com.example.inventory.dto.BranchDtos;
import com.example.inventory.entity.Branch;
import com.example.inventory.exception.DuplicateResourceException;
import com.example.inventory.exception.ResourceNotFoundException;
import com.example.inventory.repository.BranchRepository;
import com.example.inventory.repository.UserRepository;
import com.example.inventory.security.CurrentUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;
    private final AuditService auditService;

    @Override
    public BranchDtos.Response create(BranchDtos.Request request) {
        branchRepository.findByNameIgnoreCase(request.name().trim()).ifPresent(branch -> {
            throw new DuplicateResourceException("Branch name is already in use");
        });
        Branch branch = new Branch();
        apply(branch, request);
        branch = branchRepository.save(branch);
        audit("CREATE", branch, "Branch created: " + branch.getName());
        return response(branch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchDtos.Response> getAll() {
        if (currentUser.isAdmin()) return branchRepository.findAll().stream().map(this::response).toList();
        return branchRepository.findById(currentUser.require().branchId()).stream().map(this::response).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BranchDtos.Response getById(Long id) {
        currentUser.checkBranch(id);
        return response(find(id));
    }

    @Override
    public BranchDtos.Response update(Long id, BranchDtos.Request request) {
        Branch branch = find(id);
        if (!branch.getName().equalsIgnoreCase(request.name().trim())) {
            branchRepository.findByNameIgnoreCase(request.name().trim()).ifPresent(existing -> {
                throw new DuplicateResourceException("Branch name is already in use");
            });
        }
        apply(branch, request);
        audit("UPDATE", branch, "Branch updated: " + branch.getName());
        return response(branch);
    }

    @Override
    public void delete(Long id) {
        Branch branch = find(id);
        audit("DELETE", branch, "Branch deleted: " + branch.getName());
        branchRepository.delete(branch);
    }

    private void apply(Branch branch, BranchDtos.Request request) {
        branch.setName(request.name().trim());
        branch.setLocation(request.location().trim());
        branch.setDescription(request.description());
    }
    private Branch find(Long id) {
        currentUser.checkBranch(id);
        return branchRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Branch " + id + " was not found"));
    }
    private BranchDtos.Response response(Branch branch) {
        return new BranchDtos.Response(branch.getId(), branch.getName(), branch.getLocation(), branch.getDescription());
    }
    private void audit(String action, Branch branch, String description) {
        auditService.record(userRepository.getReferenceById(currentUser.require().id()), action, "Branch", branch.getId(), description);
    }
}
