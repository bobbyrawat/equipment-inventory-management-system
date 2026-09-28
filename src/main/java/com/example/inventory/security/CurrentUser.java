package com.example.inventory.security;

import com.example.inventory.exception.BadRequestException;
import com.example.inventory.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    public InventoryPrincipal require() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof InventoryPrincipal principal)) {
            throw new BadRequestException("Authenticated user context is unavailable");
        }
        return principal;
    }

    public boolean isAdmin() { return require().role() == com.example.inventory.entity.Role.ADMIN; }

    public void checkBranch(Long branchId) {
        InventoryPrincipal principal = require();
        if (principal.role() != com.example.inventory.entity.Role.ADMIN
                && !branchId.equals(principal.branchId())) {
            throw new ResourceNotFoundException("Resource was not found");
        }
    }
}
