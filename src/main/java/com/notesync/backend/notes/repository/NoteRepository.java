package com.notesync.backend.notes.repository;

import com.notesync.backend.notes.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByOwnerId(Long ownerId);

    @Query("""
            SELECT DISTINCT n FROM Note n
            WHERE n.owner.id = :userId
               OR n.id IN (
                   SELECT ns.note.id FROM NoteShare ns WHERE ns.user.id = :userId
               )
            ORDER BY n.updatedAt DESC
            """)
    List<Note> findAllAccessibleByUser(@Param("userId") Long userId);
}
