package com.smartnotes.app.backend.exception;

import java.util.UUID;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(String message) {
        super(message);
    }

    public TagNotFoundException(UUID tagId) {
        super("Tag not found with id: " + tagId);
    }
}
