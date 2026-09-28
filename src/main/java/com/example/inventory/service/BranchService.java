package com.example.inventory.service;

import com.example.inventory.dto.BranchDtos;
import java.util.List;

public interface BranchService {
    BranchDtos.Response create(BranchDtos.Request request);
    List<BranchDtos.Response> getAll();
    BranchDtos.Response getById(Long id);
    BranchDtos.Response update(Long id, BranchDtos.Request request);
    void delete(Long id);
}
