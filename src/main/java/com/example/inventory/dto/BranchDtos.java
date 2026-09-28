package com.example.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class BranchDtos {
    private BranchDtos() { }
    public record Request(@NotBlank @Size(max = 120) String name,
                          @NotBlank @Size(max = 200) String location,
                          @Size(max = 1000) String description) { }
    public record Response(Long id, String name, String location, String description) { }
}
