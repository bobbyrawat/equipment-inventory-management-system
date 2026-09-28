package com.example.inventory.controller;

import com.example.inventory.dto.AuthDtos;
import com.example.inventory.service.UserAdminService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserAdminService userService;

    @GetMapping
    public List<AuthDtos.UserResponse> getAll() { return userService.getAll(); }

    @GetMapping("/{id}")
    public AuthDtos.UserResponse getById(@PathVariable Long id) { return userService.getById(id); }

    @PostMapping
    public AuthDtos.UserResponse create(@Valid @RequestBody AuthDtos.AdminCreateUserRequest request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    public AuthDtos.UserResponse update(@PathVariable Long id,
            @Valid @RequestBody AuthDtos.AdminCreateUserRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
