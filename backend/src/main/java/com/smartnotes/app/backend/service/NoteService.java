package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.entity.Note;
import com.smartnotes.app.backend.repository.NoteRepository;
import com.smartnotes.app.backend.request.NoteRequest;
import com.smartnotes.app.backend.response.NoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteResponse createNote(NoteRequest request) {
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setDescription(request.getDescription());
        
        Note savedNote = noteRepository.save(note);
        return mapToResponse(savedNote);
    }

    public NoteResponse getNoteById(UUID id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));
        return mapToResponse(note);
    }

    public List<NoteResponse> getAllNotes() {
        return StreamSupport.stream(noteRepository.findAll().spliterator(), false)
                .map(this::mapToResponse)
                .toList();
    }

    public NoteResponse updateNote(UUID id, NoteRequest request) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));
        
        note.setTitle(request.getTitle());
        note.setDescription(request.getDescription());
        
        Note updatedNote = noteRepository.save(note);
        return mapToResponse(updatedNote);
    }

    public void deleteNote(UUID id) {
        if (!noteRepository.existsById(id)) {
            throw new RuntimeException("Note not found with id: " + id);
        }
        noteRepository.deleteById(id);
    }

    public long countNotes() {
        return noteRepository.count();
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
