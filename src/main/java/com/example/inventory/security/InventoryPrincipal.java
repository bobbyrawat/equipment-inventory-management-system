package com.example.inventory.security;

import com.example.inventory.entity.Role;
import com.example.inventory.entity.User;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record InventoryPrincipal(Long id, String username, String password, Role role, Long branchId)
        implements UserDetails {

    public static InventoryPrincipal from(User user) {
        return new InventoryPrincipal(user.getId(), user.getUsername(), user.getPassword(), user.getRole(),
                user.getBranch() == null ? null : user.getBranch().getId());
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
