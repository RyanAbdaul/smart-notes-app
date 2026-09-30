package com.smartnotes.app.backend.controller;

import com.smartnotes.app.backend.exception.TooManyAttemptsException;
import com.smartnotes.app.backend.request.NoteRequest;
import com.smartnotes.app.backend.request.UpdateNoteRequest;
import com.smartnotes.app.backend.response.NoteResponse;
import com.smartnotes.app.backend.service.NoteService;
import com.smartnotes.app.backend.service.RateLimiterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notes")
@AllArgsConstructor
@Tag(name = "Notes", description = "Note management API - Create, read, update and delete notes")
public class NoteController {

    private final NoteService noteService;
    private final RateLimiterService rateLimiterService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new note",
            description = "Creates a new note with title and description. The note will be associated with the authenticated user."
    )
    public NoteResponse createNote(@Valid @RequestBody NoteRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        if (!rateLimiterService.isAllowed("create_note", ip)) {
            throw new RuntimeException("Too many note creation attempts, try again later");
        }
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
            description = "Retrieves a list of all notes belonging to the authenticated user. Can filter by note ID and search in title/description."
    )
    public List<NoteResponse> getAllNotes(@RequestParam(required = false) UUID id,
                                          @RequestParam(required = false) String search,
                                        HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        if (!rateLimiterService.isAllowed("get_all_notes", ip, rateLimiterService.getRelaxedBandwidth())) {
            throw new TooManyAttemptsException("Too many attempts, try again later");
        }
        return noteService.getAllNotes(id, search);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update a note",
            description = "Updates an existing note's title and description. Only the note owner can update it."
    )

    public NoteResponse updateNote(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateNoteRequest request) {
        return noteService.updateNote(id, request);
    }

    @PatchMapping("/{id}/pin")
    @Operation(
            summary = "Pin a note",
            description = "Pin or unpin an existing note. Only the note owner can pin or unpin it."
    )
    public void pinNote(@PathVariable UUID id) {
        noteService.pinNote(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a note",
            description = "Permanently deletes a note from the system. Only the note owner can delete it."
    )
    public void deleteNote(@PathVariable UUID id, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        if (!rateLimiterService.isAllowed("delete_note", ip)) {
            throw new RuntimeException("Too many note deletion attempts, try again later");
        }
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
