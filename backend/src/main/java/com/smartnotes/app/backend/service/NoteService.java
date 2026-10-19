package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.annotation.RequireOwnership;
import com.smartnotes.app.backend.entity.Note;
import com.smartnotes.app.backend.entity.Notebook;
import com.smartnotes.app.backend.entity.Tag;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.exception.NoteNotFoundException;
import com.smartnotes.app.backend.exception.UnauthorizedTagAccessException;
import com.smartnotes.app.backend.exception.UserNotAuthenticatedException;
import com.smartnotes.app.backend.repository.NoteRepository;
import com.smartnotes.app.backend.repository.NotebookRepository;
import com.smartnotes.app.backend.repository.TagRepository;
import com.smartnotes.app.backend.request.NoteRequest;
import com.smartnotes.app.backend.request.UpdateNoteRequest;
import com.smartnotes.app.backend.response.NoteResponse;
import com.smartnotes.app.backend.response.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@RequireOwnership(entity = Note.class)
public class NoteService {

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;
    private final NotebookRepository notebookRepository;

    public NoteResponse createNote(NoteRequest request) {
        User currentUser = getCurrentUser();
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setDescription(request.getDescription());
        note.setPinned(false);
        note.setOwner(currentUser);

        Note savedNote = noteRepository.save(note);
        return mapToResponse(savedNote);
    }


    @Cacheable(
            value = "noteById",
            key = "#root.target.getCurrentUser().id + ':' + #id"
    )
    public NoteResponse getNoteById(UUID id) {

        User currentUser = getCurrentUser();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        return mapToResponse(note);
    }

    public List<NoteResponse> getNotesByTagsName(String name){
        return noteRepository.findDistinctByTagsNameIgnoreCase(name).stream().map(this::mapToResponse).toList();
    }

    public List<NoteResponse> getAllNotes(UUID id, String search) {
        User currentUser = getCurrentUser();

        return noteRepository.findNotes(currentUser.getId(), id, search)
                .stream()
                .sorted((n1, n2) -> Boolean.compare(n2.isPinned(), n1.isPinned()))
                .map(this::mapToResponse)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Caching(
            put = {
                    @CachePut(
                            value = "noteById",
                            key = "#root.target.getCurrentUser().id + ':' + #id"
                    )
            },
            evict = {
                    @CacheEvict(value = "allNotes", allEntries = true)
            }
    )
    public NoteResponse updateNote(UUID id, UpdateNoteRequest request) {

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
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


    @Caching(evict = {
            @CacheEvict(
                    value = "noteById",
                    key = "#root.target.getCurrentUser().id + ':' + #id"
            ),
            @CacheEvict(value = "allNotes", allEntries = true),
            @CacheEvict(value = "noteCounts", allEntries = true)
    })
    public void deleteNote(UUID id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        noteRepository.delete(note);
    }

    @Cacheable(
            value = "noteCounts",
            key = "#root.target.getCurrentUser().id"
    )
    public long countNotes() {

        User currentUser = getCurrentUser();
        return noteRepository.countByOwnerId(currentUser.getId());
    }

    @Caching(
            put = {
                    @CachePut(
                            value = "noteById",
                            key = "#root.target.getCurrentUser().id + ':' + #id"
                    )
            },
            evict = {
                    @CacheEvict(value = "allNotes", allEntries = true)
            }
    )
    public NoteResponse pinNote(UUID id){
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        note.setPinned(!note.isPinned());

        Note updatedNote = noteRepository.save(note);

        return mapToResponse(updatedNote);
    }


    public User getCurrentUser() {
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
        response.setTags(note.getTags().stream().map(t -> new TagResponse(t.getId(), t.getName())).toList());
        response.setCreatedAt(note.getCreatedAt());
        response.setUpdatedAt(note.getUpdatedAt());
        response.setImageUrl(note.getImageUrl());
        response.setPinned(note.isPinned());
        return response;
    }


    public void linkNoteToTag(UUID tagId, UUID noteId) {

        User currentUser = getCurrentUser();
        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new NoteNotFoundException("Note with id: " + noteId + " Does not exist")
        );

        Tag tag = tagRepository.findById(tagId).orElseThrow(
                () -> new NoteNotFoundException("Tag with id: " + tagId + " Does not exist")
        );

        if (!tag.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedTagAccessException("Unauthorized");
        }

        if (note.getTags() == null) {
            note.setTags(new ArrayList<>());
        }

        if (!note.getTags().contains(tag)) {
            note.getTags().add(tag);
        } else {
            note.getTags().remove(tag);
        }

        noteRepository.save(note);
    }

    public void linkNoteToNotebook(UUID notebookId, UUID noteId) {

        User currentUser = getCurrentUser();

        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new NoteNotFoundException("Note with id: " + noteId + " Does not exist")
        );

        Notebook notebook = notebookRepository.findById(notebookId).orElseThrow(
                () -> new NoteNotFoundException("Notebook with id: " + notebookId + " Does not exist")
        );

        if (!notebook.getOwner().getId().equals(currentUser.getId()) || (!note.getOwner().getId().equals(currentUser.getId()))) {
            throw new UnauthorizedTagAccessException("Unauthorized");
        }

        if (!notebook.getNotes().contains(note)) {
            notebook.getNotes().add(note);
            note.setNotebook(notebook);
        } else {
            notebook.getNotes().remove(note);
            note.setNotebook(null);
        }

        noteRepository.save(note);
        notebookRepository.save(notebook);
    }
}
