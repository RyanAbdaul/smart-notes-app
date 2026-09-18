package com.smartnotes.app.backend.exception;

import java.util.UUID;

public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(String message) {
        super(message);
    }

    public NoteNotFoundException(UUID noteId) {
        super("Note not found with id: " + noteId);
    }
}
