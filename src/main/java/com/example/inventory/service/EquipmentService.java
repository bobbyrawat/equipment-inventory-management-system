package com.example.inventory.service;

import com.example.inventory.dto.EquipmentDtos;
import java.util.List;

public interface EquipmentService {
    EquipmentDtos.Response create(EquipmentDtos.Request request);
    List<EquipmentDtos.Response> getAll();
    EquipmentDtos.Response getById(Long id);
    List<EquipmentDtos.Response> search(String query);
    EquipmentDtos.Response update(Long id, EquipmentDtos.Request request);
    void delete(Long id);
}
