package com.notesync.backend.sharing.dto;

import com.notesync.backend.sharing.entity.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShareRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "Permission is required")
    private Permission permission;
}
