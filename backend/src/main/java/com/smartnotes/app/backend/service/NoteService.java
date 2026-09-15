package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.entity.Note;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.exception.NoteNotFoundException;
import com.smartnotes.app.backend.exception.UnauthorizedNoteAccessException;
import com.smartnotes.app.backend.exception.UserNotAuthenticatedException;
import com.smartnotes.app.backend.repository.NoteRepository;
import com.smartnotes.app.backend.request.NoteRequest;
import com.smartnotes.app.backend.response.NoteResponse;
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

    public NoteResponse createNote(NoteRequest request) {
        User currentUser = getCurrentUser();
        
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setDescription(request.getDescription());
        note.setOwner(currentUser);
        
        Note savedNote = noteRepository.save(note);
        return mapToResponse(savedNote);
    }

    public NoteResponse getNoteById(UUID id) {
        User currentUser = getCurrentUser();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + id));
        
        if (note.getOwner().getId() != currentUser.getId()) {
            throw new UnauthorizedNoteAccessException("You don't have permission to access this note");
        }
        
        return mapToResponse(note);
    }

    public List<NoteResponse> getAllNotes() {
        User currentUser = getCurrentUser();
        return StreamSupport.stream(noteRepository.findAll().spliterator(), false)
                .filter(note -> note.getOwner().getId() == currentUser.getId())
                .map(this::mapToResponse)
                .toList();
    }

    public NoteResponse updateNote(UUID id, NoteRequest request) {
        User currentUser = getCurrentUser();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + id));
        
        if (note.getOwner().getId() != currentUser.getId()) {
            throw new UnauthorizedNoteAccessException("You don't have permission to update this note");
        }
        
        note.setTitle(request.getTitle());
        note.setDescription(request.getDescription());
        
        Note updatedNote = noteRepository.save(note);
        return mapToResponse(updatedNote);
    }

    public void deleteNote(UUID id) {
        User currentUser = getCurrentUser();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + id));
        
        if (note.getOwner().getId() != currentUser.getId()) {
            throw new UnauthorizedNoteAccessException("You don't have permission to delete this note");
        }
        
        noteRepository.deleteById(id);
    }

    public long countNotes() {
        User currentUser = getCurrentUser();
        return StreamSupport.stream(noteRepository.findAll().spliterator(), false)
                .filter(note -> note.getOwner().getId() == currentUser.getId())
                .count();
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
        return response;
    }
}
