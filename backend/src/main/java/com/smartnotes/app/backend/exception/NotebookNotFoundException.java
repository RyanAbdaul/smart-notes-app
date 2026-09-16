package com.smartnotes.app.backend.exception;

public class NotebookNotFoundException extends RuntimeException {
    public NotebookNotFoundException(String message) {
        super(message);
    }
}