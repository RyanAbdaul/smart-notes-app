package com.smartnotes.app.backend.rest;

import com.smartnotes.app.backend.request.NoteRequest;
import com.smartnotes.app.backend.response.NoteResponse;
import com.smartnotes.app.backend.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Tag(name = "Notes", description = "Note management API - Create, read, update and delete notes")
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    @Operation(summary = "Create a new note", description = "Creates a new note with title and description")
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody NoteRequest request) {
        NoteResponse response = noteService.createNote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get note by ID", description = "Retrieves a single note by its unique identifier")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable UUID id) {
        NoteResponse response = noteService.getNoteById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all notes", description = "Retrieves a list of all notes in the system")
    public ResponseEntity<List<NoteResponse>> getAllNotes() {
        List<NoteResponse> notes = noteService.getAllNotes();
        return ResponseEntity.ok(notes);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a note", description = "Updates an existing note's title and description")
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable UUID id,
            @Valid @RequestBody NoteRequest request) {
        NoteResponse response = noteService.updateNote(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a note", description = "Permanently deletes a note from the system")
    public ResponseEntity<Void> deleteNote(@PathVariable UUID id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @Operation(summary = "Count total notes", description = "Returns the total number of notes in the system")
    public ResponseEntity<Long> countNotes() {
        long count = noteService.countNotes();
        return ResponseEntity.ok(count);
    }
}
