package com.smartnotes.app.backend.rest;

import com.smartnotes.app.backend.request.NoteRequest;
import com.smartnotes.app.backend.response.NoteResponse;
import com.smartnotes.app.backend.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new note",
            description = "Creates a new note with title and description. The note will be associated with the authenticated user."
    )
    public NoteResponse createNote(@Valid @RequestBody NoteRequest request) {
        return noteService.createNote(request);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get note by ID",
            description = "Retrieves a single note by its unique identifier. Only returns the note if it belongs to the authenticated user."
    )
    public NoteResponse getNoteById(@PathVariable UUID id) {
        return noteService.getNoteById(id);
    }

    @GetMapping
    @Operation(
            summary = "Get all notes",
            description = "Retrieves a list of all notes belonging to the authenticated user"
    )
    public List<NoteResponse> getAllNotes() {
        return noteService.getAllNotes();
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update a note",
            description = "Updates an existing note's title and description. Only the note owner can update it."
    )
    public NoteResponse updateNote(
            @PathVariable UUID id,
            @Valid @RequestBody NoteRequest request) {
        return noteService.updateNote(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a note",
            description = "Permanently deletes a note from the system. Only the note owner can delete it."
    )
    public void deleteNote(@PathVariable UUID id) {
        noteService.deleteNote(id);
    }

    @GetMapping("/count")
    @Operation(
            summary = "Count total notes",
            description = "Returns the total number of notes belonging to the authenticated user"
    )
    public long countNotes() {
        return noteService.countNotes();
    }
}
