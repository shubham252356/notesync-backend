package com.notesync.backend.sharing.repository;

import com.notesync.backend.sharing.entity.NoteShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteShareRepository extends JpaRepository<NoteShare, Long> {

    List<NoteShare> findByNoteId(Long noteId);

    Optional<NoteShare> findByNoteIdAndUserId(Long noteId, Long userId);

    boolean existsByNoteIdAndUserId(Long noteId, Long userId);

    void deleteByNoteIdAndUserId(Long noteId, Long userId);
}
