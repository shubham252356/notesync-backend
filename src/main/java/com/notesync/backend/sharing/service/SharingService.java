package com.notesync.backend.sharing.service;

import com.notesync.backend.common.entity.User;
import com.notesync.backend.common.exception.DuplicateResourceException;
import com.notesync.backend.common.exception.ResourceNotFoundException;
import com.notesync.backend.common.exception.UnauthorizedAccessException;
import com.notesync.backend.common.repository.UserRepository;
import com.notesync.backend.notes.entity.Note;
import com.notesync.backend.notes.repository.NoteRepository;
import com.notesync.backend.sharing.dto.ShareRequest;
import com.notesync.backend.sharing.dto.ShareResponse;
import com.notesync.backend.sharing.entity.NoteShare;
import com.notesync.backend.sharing.repository.NoteShareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SharingService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteShareRepository noteShareRepository;

    @Transactional
    public ShareResponse shareNote(String ownerUsername, Long noteId, ShareRequest request) {
        Note note = getNote(noteId);
        assertOwner(ownerUsername, note);

        User targetUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUsername()));

        if (targetUser.getUsername().equals(ownerUsername)) {
            throw new DuplicateResourceException("Cannot share a note with yourself");
        }

        if (noteShareRepository.existsByNoteIdAndUserId(noteId, targetUser.getId())) {
            throw new DuplicateResourceException("Note is already shared with user: " + request.getUsername());
        }

        NoteShare share = NoteShare.builder()
                .note(note)
                .user(targetUser)
                .permission(request.getPermission())
                .build();

        share = noteShareRepository.save(share);
        log.info("Shared note id={} with user={} permission={}", noteId, request.getUsername(), request.getPermission());
        return toResponse(share);
    }

    @Transactional(readOnly = true)
    public List<ShareResponse> getNoteUsers(String requesterUsername, Long noteId) {
        Note note = getNote(noteId);
        assertOwner(requesterUsername, note);
        return noteShareRepository.findByNoteId(noteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void assertOwner(String username, Note note) {
        if (!note.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Only the note owner can manage sharing");
        }
    }

    private Note getNote(Long noteId) {
        return noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));
    }

    private ShareResponse toResponse(NoteShare share) {
        return ShareResponse.builder()
                .id(share.getId())
                .noteId(share.getNote().getId())
                .userId(share.getUser().getId())
                .username(share.getUser().getUsername())
                .permission(share.getPermission())
                .createdAt(share.getCreatedAt())
                .build();
    }
}
