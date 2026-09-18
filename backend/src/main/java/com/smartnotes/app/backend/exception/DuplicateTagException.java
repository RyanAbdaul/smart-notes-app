package com.smartnotes.app.backend.exception;

import java.util.UUID;

public class DuplicateTagException extends RuntimeException {
    public DuplicateTagException(String message) {
        super(message);
    }

    public DuplicateTagException(String tagName, UUID userId) {
        super("Tag with name '" + tagName + "' already exists for user with id: " + userId);
    }
}