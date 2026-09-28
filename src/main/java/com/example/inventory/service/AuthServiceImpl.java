package com.example.inventory.service;

import com.example.inventory.dto.AuthDtos;
import com.example.inventory.entity.Role;
import com.example.inventory.entity.User;
import com.example.inventory.exception.DuplicateResourceException;
import com.example.inventory.exception.ResourceNotFoundException;
import com.example.inventory.repository.BranchRepository;
import com.example.inventory.repository.UserRepository;
import com.example.inventory.security.InventoryPrincipal;
import com.example.inventory.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditService auditService;

    @Override
    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        ensureUnique(request.username(), request.email());
        User user = new User();
        user.setName(request.name().trim());
        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.LOGISTICS_OFFICER);
        user.setBranch(branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch " + request.branchId() + " was not found")));
        user = userRepository.save(user);
        auditService.record(user, "REGISTER", "User", user.getId(), "New logistics officer registered");
        return createResponse(InventoryPrincipal.from(user));
    }

    @Override
    @Transactional
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.usernameOrEmail(), request.password()));
        InventoryPrincipal principal = (InventoryPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.id())
                .orElseThrow(() -> new UsernameNotFoundException("User account was not found"));
        auditService.record(user, "LOGIN", "User", user.getId(), "Successful login");
        return createResponse(principal);
    }

    private void ensureUnique(String username, String email) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateResourceException("Username is already in use");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email address is already in use");
        }
    }

    private AuthDtos.AuthResponse createResponse(InventoryPrincipal principal) {
        User user = userRepository.findById(principal.id())
                .orElseThrow(() -> new ResourceNotFoundException("User account was not found"));
        AuthDtos.UserResponse responseUser = new AuthDtos.UserResponse(user.getId(), user.getName(), user.getUsername(),
                user.getEmail(), user.getRole(), user.getBranch() == null ? null : user.getBranch().getId());
        return new AuthDtos.AuthResponse(jwtService.generateToken(principal), "Bearer", jwtService.getExpirationMs(), responseUser);
    }
}
