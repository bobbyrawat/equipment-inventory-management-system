package com.example.inventory.service;

import com.example.inventory.dto.AuthDtos;
import com.example.inventory.entity.Branch;
import com.example.inventory.entity.User;
import com.example.inventory.exception.DuplicateResourceException;
import com.example.inventory.exception.ResourceNotFoundException;
import com.example.inventory.repository.BranchRepository;
import com.example.inventory.repository.UserRepository;
import com.example.inventory.security.CurrentUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminServiceImpl implements UserAdminService {
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;
    private final AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public List<AuthDtos.UserResponse> getAll() {
        return userRepository.findAll().stream().map(this::response).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthDtos.UserResponse getById(Long id) {
        return response(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " was not found")));
    }

    @Override
    public AuthDtos.UserResponse create(AuthDtos.AdminCreateUserRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new DuplicateResourceException("Username is already in use");
        }
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("Email address is already in use");
        }
        Branch branch = request.branchId() == null ? null : branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch " + request.branchId() + " was not found"));
        if (request.role() != com.example.inventory.entity.Role.ADMIN && branch == null) {
            throw new com.example.inventory.exception.BadRequestException("A branch is required for non-admin users");
        }
        User user = new User();
        user.setName(request.name().trim());
        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setBranch(branch);
        user = userRepository.save(user);
        auditService.record(userRepository.getReferenceById(currentUser.require().id()), "CREATE", "User", user.getId(),
                "User account created: " + user.getUsername());
        return response(user);
    }

    @Override
    public AuthDtos.UserResponse update(Long id, AuthDtos.AdminCreateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " was not found"));
        if (userRepository.existsByUsernameIgnoreCaseAndIdNot(request.username(), id)) {
            throw new DuplicateResourceException("Username is already in use");
        }
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("Email address is already in use");
        }
        Branch branch = request.branchId() == null ? null : branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch " + request.branchId() + " was not found"));
        if (request.role() != com.example.inventory.entity.Role.ADMIN && branch == null) {
            throw new com.example.inventory.exception.BadRequestException("A branch is required for non-admin users");
        }
        user.setName(request.name().trim());
        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setBranch(branch);
        auditService.record(userRepository.getReferenceById(currentUser.require().id()), "UPDATE", "User", user.getId(),
                "User account updated: " + user.getUsername());
        return response(user);
    }

    @Override
    public void delete(Long id) {
        if (currentUser.require().id().equals(id)) {
            throw new com.example.inventory.exception.BadRequestException("Administrators cannot delete their own account");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " was not found"));
        auditService.record(userRepository.getReferenceById(currentUser.require().id()), "DELETE", "User", id,
                "User account deleted: " + user.getUsername());
        userRepository.delete(user);
    }

    private AuthDtos.UserResponse response(User user) {
        return new AuthDtos.UserResponse(user.getId(), user.getName(), user.getUsername(), user.getEmail(), user.getRole(),
                user.getBranch() == null ? null : user.getBranch().getId());
    }
}
