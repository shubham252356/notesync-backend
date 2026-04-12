package com.notesync.backend.notes.controller;

import com.notesync.backend.notes.dto.NoteRequest;
import com.notesync.backend.notes.dto.NoteResponse;
import com.notesync.backend.notes.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody NoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteService.createNote(userDetails.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getNotes(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(noteService.getAccessibleNotes(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(noteService.getNoteById(userDetails.getUsername(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody NoteRequest request) {
        return ResponseEntity.ok(noteService.updateNote(userDetails.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        noteService.deleteNote(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
