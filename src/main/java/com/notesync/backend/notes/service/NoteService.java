package com.notesync.backend.notes.service;

import com.notesync.backend.common.entity.User;
import com.notesync.backend.common.exception.ResourceNotFoundException;
import com.notesync.backend.common.exception.UnauthorizedAccessException;
import com.notesync.backend.common.repository.UserRepository;
import com.notesync.backend.notes.dto.NoteRequest;
import com.notesync.backend.notes.dto.NoteResponse;
import com.notesync.backend.notes.entity.Note;
import com.notesync.backend.notes.repository.NoteRepository;
import com.notesync.backend.sharing.entity.NoteShare;
import com.notesync.backend.sharing.entity.Permission;
import com.notesync.backend.sharing.repository.NoteShareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteShareRepository noteShareRepository;

    @Transactional
    public NoteResponse createNote(String username, NoteRequest request) {
        User owner = getUser(username);
        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .owner(owner)
                .build();
        note = noteRepository.save(note);
        log.info("Created note id={} for user={}", note.getId(), username);
        return toResponse(note);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getAccessibleNotes(String username) {
        User user = getUser(username);
        return noteRepository.findAllAccessibleByUser(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public NoteResponse getNoteById(String username, Long noteId) {
        Note note = getNote(noteId);
        assertReadAccess(username, note);
        return toResponse(note);
    }

    @Transactional
    public NoteResponse updateNote(String username, Long noteId, NoteRequest request) {
        Note note = getNote(noteId);
        assertWriteAccess(username, note);
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note = noteRepository.save(note);
        log.info("Updated note id={} by user={}", noteId, username);
        return toResponse(note);
    }

    @Transactional
    public void deleteNote(String username, Long noteId) {
        Note note = getNote(noteId);
        assertOwner(username, note);
        noteRepository.delete(note);
        log.info("Deleted note id={} by user={}", noteId, username);
    }

    // --- Authorization helpers ---

    private void assertReadAccess(String username, Note note) {
        if (isOwner(username, note)) return;
        Optional<NoteShare> share = noteShareRepository.findByNoteIdAndUserId(note.getId(), getUser(username).getId());
        if (share.isEmpty()) {
            throw new UnauthorizedAccessException("You do not have access to this note");
        }
    }

    private void assertWriteAccess(String username, Note note) {
        if (isOwner(username, note)) return;
        User user = getUser(username);
        NoteShare share = noteShareRepository.findByNoteIdAndUserId(note.getId(), user.getId())
                .orElseThrow(() -> new UnauthorizedAccessException("You do not have access to this note"));
        if (share.getPermission() != Permission.WRITE) {
            throw new UnauthorizedAccessException("You do not have write access to this note");
        }
    }

    private void assertOwner(String username, Note note) {
        if (!isOwner(username, note)) {
            throw new UnauthorizedAccessException("Only the note owner can perform this action");
        }
    }

    private boolean isOwner(String username, Note note) {
        return note.getOwner().getUsername().equals(username);
    }

    private Note getNote(Long noteId) {
        return noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private NoteResponse toResponse(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .ownerId(note.getOwner().getId())
                .ownerUsername(note.getOwner().getUsername())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}
