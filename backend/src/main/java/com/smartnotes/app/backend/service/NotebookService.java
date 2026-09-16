package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.entity.Notebook;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.exception.NotebookNotFoundException;
import com.smartnotes.app.backend.exception.UnauthorizedNotebookAccessException;
import com.smartnotes.app.backend.exception.UserNotAuthenticatedException;
import com.smartnotes.app.backend.request.NotebookRequest;
import com.smartnotes.app.backend.repository.NotebookRepository;
import com.smartnotes.app.backend.response.NotebookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotebookService {

    private final NotebookRepository notebookRepository;

    public List<NotebookResponse> getAllNotebooks() {
        User currentUser = getCurrentUser();
        List<Notebook> notebooks = notebookRepository.findAllWithNotesByOwnerId(currentUser.getId());
        return notebooks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public NotebookResponse createNotebook(NotebookRequest request) {
        User currentUser = getCurrentUser();
        Notebook notebook = new Notebook();
        notebook.setName(request.getName());
        notebook.setOwner(currentUser);
        Notebook savedNotebook = notebookRepository.save(notebook);
        return mapToResponse(savedNotebook);
    }

    public NotebookResponse getNotebookById(UUID id) {
        User currentUser = getCurrentUser();
        Notebook notebook = notebookRepository.findById(id)
                .orElseThrow(() -> new NotebookNotFoundException("Notebook not found with id: " + id));
        if (notebook.getOwner().getId() != currentUser.getId()) {
            throw new UnauthorizedNotebookAccessException("You don't have permission to access this notebook");
        }
        return mapToResponse(notebook);
    }

    public NotebookResponse updateNotebook(UUID id, NotebookRequest request) {
        User currentUser = getCurrentUser();
        Notebook notebook = notebookRepository.findById(id)
                .orElseThrow(() -> new NotebookNotFoundException("Notebook not found with id: " + id));
        if (notebook.getOwner().getId() != currentUser.getId()) {
            throw new UnauthorizedNotebookAccessException("You don't have permission to update this notebook");
        }
        notebook.setName(request.getName());
        Notebook updatedNotebook = notebookRepository.save(notebook);
        return mapToResponse(updatedNotebook);
    }

    public void deleteNotebook(UUID id) {
        User currentUser = getCurrentUser();
        Notebook notebook = notebookRepository.findById(id)
                .orElseThrow(() -> new NotebookNotFoundException("Notebook not found with id: " + id));
        if (notebook.getOwner().getId() != currentUser.getId()) {
            throw new UnauthorizedNotebookAccessException("You don't have permission to delete this notebook");
        }
        notebookRepository.deleteById(id);
    }

    public long countNotebooks() {
        User currentUser = getCurrentUser();
        return notebookRepository.countByOwnerId(currentUser.getId());
    }

    private NotebookResponse mapToResponse(Notebook notebook) {
        NotebookResponse response = new NotebookResponse();
        response.setId(notebook.getId());
        response.setName(notebook.getName());
        response.setNotes(notebook.getNotes().stream()
                .map(this::mapNoteToResponse)
                .collect(Collectors.toList()));
        response.setCreatedAt(notebook.getCreatedAt());
        response.setUpdatedAt(notebook.getUpdatedAt());
        return response;
    }

    private NoteResponse mapNoteToResponse(Note note) {
        NoteResponse response = new NoteResponse();
        response.setId(note.getId());
        response.setTitle(note.getTitle());
        response.setDescription(note.getDescription());
        response.setCreatedAt(note.getCreatedAt());
        response.setUpdatedAt(note.getUpdatedAt());
        return response;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }
        return (User) authentication.getPrincipal();
    }
}