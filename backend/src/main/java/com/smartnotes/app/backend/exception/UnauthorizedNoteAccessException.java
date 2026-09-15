package com.smartnotes.app.backend.exception;

public class UnauthorizedNoteAccessException extends RuntimeException {
    public UnauthorizedNoteAccessException(String message) {
        super(message);
    }
}
