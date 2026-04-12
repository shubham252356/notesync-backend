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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NoteShareRepository noteShareRepository;

    @InjectMocks
    private NoteService noteService;

    private User owner;
    private Note note;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).username("alice").email("alice@example.com").build();
        note = Note.builder()
                .id(10L)
                .title("My Note")
                .content(Map.of("text", "Hello World"))
                .owner(owner)
                .build();
    }

    @Test
    void createNote_success() {
        NoteRequest request = new NoteRequest();
        request.setTitle("My Note");
        request.setContent(Map.of("text", "Hello World"));

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(owner));
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        NoteResponse response = noteService.createNote("alice", request);

        assertThat(response.getTitle()).isEqualTo("My Note");
        assertThat(response.getOwnerId()).isEqualTo(1L);
        assertThat(response.getOwnerUsername()).isEqualTo("alice");
    }

    @Test
    void getAccessibleNotes_returnsOwnedAndShared() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(owner));
        when(noteRepository.findAllAccessibleByUser(1L)).thenReturn(List.of(note));

        List<NoteResponse> notes = noteService.getAccessibleNotes("alice");

        assertThat(notes).hasSize(1);
        assertThat(notes.get(0).getId()).isEqualTo(10L);
    }

    @Test
    void getNoteById_asOwner_success() {
        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        NoteResponse response = noteService.getNoteById("alice", 10L);

        assertThat(response.getId()).isEqualTo(10L);
    }

    @Test
    void getNoteById_notFound_throws() {
        when(noteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.getNoteById("alice", 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getNoteById_noAccess_throws() {
        User bob = User.builder().id(2L).username("bob").build();
        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(bob));
        when(noteShareRepository.findByNoteIdAndUserId(10L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.getNoteById("bob", 10L))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    void updateNote_asSharedWriteUser_success() {
        User bob = User.builder().id(2L).username("bob").build();
        NoteShare share = NoteShare.builder()
                .id(1L).note(note).user(bob).permission(Permission.WRITE).build();

        NoteRequest request = new NoteRequest();
        request.setTitle("Updated");
        request.setContent(Map.of("text", "Updated content"));

        Note updatedNote = Note.builder()
                .id(10L).title("Updated")
                .content(Map.of("text", "Updated content"))
                .owner(owner).build();

        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(bob));
        when(noteShareRepository.findByNoteIdAndUserId(10L, 2L)).thenReturn(Optional.of(share));
        when(noteRepository.save(any(Note.class))).thenReturn(updatedNote);

        NoteResponse response = noteService.updateNote("bob", 10L, request);

        assertThat(response.getTitle()).isEqualTo("Updated");
    }

    @Test
    void deleteNote_notOwner_throws() {
        User bob = User.builder().id(2L).username("bob").build();
        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        assertThatThrownBy(() -> noteService.deleteNote("bob", 10L))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessageContaining("owner");
    }
}
