package com.smartnotes.app.backend.exception;

import java.util.UUID;

public class UnauthorizedNoteAccessException extends RuntimeException {
    public UnauthorizedNoteAccessException(String message) {
        super(message);
    }

    public UnauthorizedNoteAccessException(UUID noteId, UUID userId) {
        super("Unauthorized access to note with id: " + noteId + " for user with id: " + userId);
    }
}
