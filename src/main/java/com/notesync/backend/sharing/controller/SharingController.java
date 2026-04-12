package com.notesync.backend.sharing.controller;

import com.notesync.backend.sharing.dto.ShareRequest;
import com.notesync.backend.sharing.dto.ShareResponse;
import com.notesync.backend.sharing.service.SharingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes/{noteId}")
@RequiredArgsConstructor
public class SharingController {

    private final SharingService sharingService;

    @PostMapping("/share")
    public ResponseEntity<ShareResponse> shareNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId,
            @Valid @RequestBody ShareRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sharingService.shareNote(userDetails.getUsername(), noteId, request));
    }

    @GetMapping("/users")
    public ResponseEntity<List<ShareResponse>> getNoteUsers(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId) {
        return ResponseEntity.ok(sharingService.getNoteUsers(userDetails.getUsername(), noteId));
    }
}
