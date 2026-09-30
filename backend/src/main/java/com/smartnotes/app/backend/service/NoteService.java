package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.entity.Note;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.exception.NoteNotFoundException;
import com.smartnotes.app.backend.exception.UnauthorizedNoteAccessException;
import com.smartnotes.app.backend.exception.UserNotAuthenticatedException;
import com.smartnotes.app.backend.repository.NoteRepository;
import com.smartnotes.app.backend.request.NoteRequest;
import com.smartnotes.app.backend.request.UpdateNoteRequest;
import com.smartnotes.app.backend.response.NoteResponse;
import com.smartnotes.app.backend.util.DebuggingTools;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final DebuggingTools debuggingTools;

    public NoteResponse createNote(NoteRequest request) {
        User currentUser = getCurrentUser();
        debuggingTools.SlowDatabaseCalls();
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setDescription(request.getDescription());
        note.setPinned(false);
        note.setOwner(currentUser);
        
        Note savedNote = noteRepository.save(note);
        return mapToResponse(savedNote);
    }

    public NoteResponse getNoteById(UUID id) {

        User currentUser = getCurrentUser();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        if (!note.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedNoteAccessException(id, currentUser.getId());
        }
        
        return mapToResponse(note);
    }

    public List<NoteResponse> getAllNotes(UUID id, String search) {
        User currentUser = getCurrentUser();
        return noteRepository.findNotes(currentUser.getId(), id, search)
                .stream()
                .sorted((n1, n2) -> Boolean.compare(n2.isPinned(), n1.isPinned()))
                .map(this::mapToResponse)
                .toList();
    }

    public NoteResponse updateNote(UUID id, UpdateNoteRequest request) {
        User currentUser = getCurrentUser();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        if (!note.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedNoteAccessException(id, currentUser.getId());
        }

        if (request.getTitle() != null){
            note.setTitle(request.getTitle());
        }

        if (request.getDescription() != null){
            note.setDescription(request.getDescription());
        }

        if (request.getImageUrl() != null){
            note.setImageUrl(request.getImageUrl());
        }

        Note updatedNote = noteRepository.save(note);

        return mapToResponse(updatedNote);
    }



    public void deleteNote(UUID id) {
        User currentUser = getCurrentUser();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        if (!note.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedNoteAccessException(id, currentUser.getId());
        }
        
        noteRepository.delete(note);
    }

    public long countNotes() {
        User currentUser = getCurrentUser();
        return noteRepository.findAll().stream()
                .filter(note -> note.getOwner().getId().equals(currentUser.getId()))
                .count();
    }


    public void pinNote(UUID id){
        User currentUser = getCurrentUser();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        if (!note.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedNoteAccessException(id, currentUser.getId());
        }

        note.setPinned(!note.isPinned());
        noteRepository.save(note);
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }
        return (User) authentication.getPrincipal();
    }


    private NoteResponse mapToResponse(Note note) {
        NoteResponse response = new NoteResponse();
        response.setId(note.getId());
        response.setTitle(note.getTitle());
        response.setDescription(note.getDescription());
        response.setCreatedAt(note.getCreatedAt());
        response.setUpdatedAt(note.getUpdatedAt());
        response.setImageUrl(note.getImageUrl());
        response.setPinned(note.isPinned());
        return response;
    }
}
