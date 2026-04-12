package com.notesync.backend.sharing.dto;

import com.notesync.backend.sharing.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareResponse {

    private Long id;
    private Long noteId;
    private Long userId;
    private String username;
    private Permission permission;
    private LocalDateTime createdAt;
}
