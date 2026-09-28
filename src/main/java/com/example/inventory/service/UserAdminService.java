package com.example.inventory.service;

import com.example.inventory.dto.AuthDtos;
import java.util.List;

public interface UserAdminService {
    List<AuthDtos.UserResponse> getAll();
    AuthDtos.UserResponse getById(Long id);
    AuthDtos.UserResponse create(AuthDtos.AdminCreateUserRequest request);
    AuthDtos.UserResponse update(Long id, AuthDtos.AdminCreateUserRequest request);
    void delete(Long id);
}
