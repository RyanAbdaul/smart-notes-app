package com.smartnotes.app.backend.exception;

import java.util.UUID;

public class DuplicateNoteException extends RuntimeException {
    public DuplicateNoteException(String message) {
        super(message);
    }

    public DuplicateNoteException(String title, UUID notebookId) {
        super("Note with title '" + title + "' already exists in notebook: " + notebookId);
    }
}
