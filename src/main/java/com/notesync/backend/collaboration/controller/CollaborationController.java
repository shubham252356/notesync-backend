package com.notesync.backend.collaboration.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Placeholder controller for real-time note collaboration over WebSocket/STOMP.
 *
 * <p>Future implementation will handle:
 * <ul>
 *   <li>Operational transform or CRDT-based conflict resolution</li>
 *   <li>Presence tracking (who is editing a note)</li>
 *   <li>Cursor position broadcasting</li>
 * </ul>
 */
@Controller
@Slf4j
public class CollaborationController {

    /**
     * Placeholder: receives note-update events and broadcasts to subscribers.
     * Message destination: /app/notes/{noteId}/edit
     * Broadcast destination: /topic/notes/{noteId}
     */
    @MessageMapping("/notes/{noteId}/edit")
    @SendTo("/topic/notes/{noteId}")
    public Object handleNoteEdit(@DestinationVariable Long noteId, Object payload) {
        log.debug("Received edit event for note id={}", noteId);
        // TODO: validate permissions, apply operational transform, persist delta
        return payload;
    }
}
