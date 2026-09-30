package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.entity.Note;
import com.smartnotes.app.backend.entity.Notebook;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.exception.DuplicateNotebookException;
import com.smartnotes.app.backend.exception.NotebookNotFoundException;
import com.smartnotes.app.backend.exception.UnauthorizedNotebookAccessException;
import com.smartnotes.app.backend.exception.UserNotAuthenticatedException;
import com.smartnotes.app.backend.repository.NoteRepository;
import com.smartnotes.app.backend.repository.NotebookRepository;
import com.smartnotes.app.backend.request.NoteRequest;
import com.smartnotes.app.backend.request.NotebookRequest;
import com.smartnotes.app.backend.response.NoteResponse;
import com.smartnotes.app.backend.response.NotebookResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotebookServiceTests {

    @Mock
    private NotebookRepository notebookRepository;

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NotebookService notebookService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane.doe@example.com");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthenticatedUser(User user) {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    // ==================== getAllNotebooks ====================

    @Test
    void NotebookService_GetAllNotebooks_NotebooksAreReturnedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        Notebook notebook1 = new Notebook();
        notebook1.setId(UUID.randomUUID());
        notebook1.setName("Work");
        notebook1.setOwner(user);
        notebook1.setNotes(new ArrayList<>());

        Notebook notebook2 = new Notebook();
        notebook2.setId(UUID.randomUUID());
        notebook2.setName("Personal");
        notebook2.setOwner(user);
        notebook2.setNotes(new ArrayList<>());

        when(notebookRepository.findAllWithNotesByOwnerId(userId)).thenReturn(List.of(notebook1, notebook2));

        // Act
        List<NotebookResponse> responses = notebookService.getAllNotebooks();

        // Assert
        Assertions.assertThat(responses).hasSize(2);
        Assertions.assertThat(responses.get(0).getName()).isEqualTo("Work");
        Assertions.assertThat(responses.get(1).getName()).isEqualTo("Personal");
        Mockito.verify(notebookRepository).findAllWithNotesByOwnerId(userId);
    }

    @Test
    void NotebookService_GetAllNotebooks_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
        // Arrange
        SecurityContextHolder.clearContext();

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.getAllNotebooks())
                .isInstanceOf(UserNotAuthenticatedException.class);
        Mockito.verify(notebookRepository, Mockito.never()).findAllWithNotesByOwnerId(Mockito.any());
    }

    // ==================== createNotebook ====================

    @Test
    void NotebookService_CreateNotebook_NotebookIsCreatedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        NotebookRequest request = new NotebookRequest("Study");
        when(notebookRepository.existsByNameAndOwnerId("Study", userId)).thenReturn(false);

        UUID notebookId = UUID.randomUUID();
        Notebook savedNotebook = new Notebook();
        savedNotebook.setId(notebookId);
        savedNotebook.setName("Study");
        savedNotebook.setOwner(user);
        savedNotebook.setNotes(new ArrayList<>());

        when(notebookRepository.save(Mockito.any(Notebook.class))).thenReturn(savedNotebook);

        // Act
        NotebookResponse response = notebookService.createNotebook(request);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(notebookId);
        Assertions.assertThat(response.getName()).isEqualTo("Study");
        Mockito.verify(notebookRepository).existsByNameAndOwnerId("Study", userId);
        Mockito.verify(notebookRepository).save(Mockito.any(Notebook.class));
    }

    @Test
    void NotebookService_CreateNotebook_DuplicateName_ThrowsDuplicateNotebookException() {
        // Arrange
        mockAuthenticatedUser(user);

        NotebookRequest request = new NotebookRequest("Study");
        when(notebookRepository.existsByNameAndOwnerId("Study", userId)).thenReturn(true);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.createNotebook(request))
                .isInstanceOf(DuplicateNotebookException.class);
        Mockito.verify(notebookRepository).existsByNameAndOwnerId("Study", userId);
        Mockito.verify(notebookRepository, Mockito.never()).save(Mockito.any(Notebook.class));
    }

    @Test
    void NotebookService_CreateNotebook_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
        // Arrange
        SecurityContextHolder.clearContext();
        NotebookRequest request = new NotebookRequest("Study");

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.createNotebook(request))
                .isInstanceOf(UserNotAuthenticatedException.class);
        Mockito.verify(notebookRepository, Mockito.never()).existsByNameAndOwnerId(Mockito.any(), Mockito.any());
    }

    // ==================== getNotebookById ====================

    @Test
    void NotebookService_GetNotebookById_NotebookIsReturnedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        UUID notebookId = UUID.randomUUID();
        Notebook notebook = new Notebook();
        notebook.setId(notebookId);
        notebook.setName("Work");
        notebook.setOwner(user);
        notebook.setNotes(new ArrayList<>());

        when(notebookRepository.findById(notebookId)).thenReturn(Optional.of(notebook));

        // Act
        NotebookResponse response = notebookService.getNotebookById(notebookId);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(notebookId);
        Assertions.assertThat(response.getName()).isEqualTo("Work");
        Mockito.verify(notebookRepository).findById(notebookId);
    }

    @Test
    void NotebookService_GetNotebookById_NotebookNotFound_ThrowsNotebookNotFoundException() {
        // Arrange
        mockAuthenticatedUser(user);
        UUID notebookId = UUID.randomUUID();
        when(notebookRepository.findById(notebookId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.getNotebookById(notebookId))
                .isInstanceOf(NotebookNotFoundException.class);
        Mockito.verify(notebookRepository).findById(notebookId);
    }

    @Test
    void NotebookService_GetNotebookById_UnauthorizedAccess_ThrowsUnauthorizedNotebookAccessException() {
        // Arrange
        mockAuthenticatedUser(user);

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        UUID notebookId = UUID.randomUUID();
        Notebook notebook = new Notebook();
        notebook.setId(notebookId);
        notebook.setName("Work");
        notebook.setOwner(anotherUser);

        when(notebookRepository.findById(notebookId)).thenReturn(Optional.of(notebook));

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.getNotebookById(notebookId))
                .isInstanceOf(UnauthorizedNotebookAccessException.class);
        Mockito.verify(notebookRepository).findById(notebookId);
    }

    @Test
    void NotebookService_GetNotebookById_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
        // Arrange
        SecurityContextHolder.clearContext();
        UUID notebookId = UUID.randomUUID();

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.getNotebookById(notebookId))
                .isInstanceOf(UserNotAuthenticatedException.class);
        Mockito.verify(notebookRepository, Mockito.never()).findById(Mockito.any());
    }

    // ==================== updateNotebook ====================

    @Test
    void NotebookService_UpdateNotebook_NotebookIsUpdatedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        UUID notebookId = UUID.randomUUID();
        Notebook notebook = new Notebook();
        notebook.setId(notebookId);
        notebook.setName("Old Name");
        notebook.setOwner(user);
        notebook.setNotes(new ArrayList<>());

        NotebookRequest request = new NotebookRequest("New Name");

        when(notebookRepository.findById(notebookId)).thenReturn(Optional.of(notebook));
        when(notebookRepository.existsByNameAndOwnerId("New Name", userId)).thenReturn(false);
        when(notebookRepository.save(Mockito.any(Notebook.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        NotebookResponse response = notebookService.updateNotebook(notebookId, request);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getName()).isEqualTo("New Name");
        Mockito.verify(notebookRepository).findById(notebookId);
        Mockito.verify(notebookRepository).existsByNameAndOwnerId("New Name", userId);
        Mockito.verify(notebookRepository).save(notebook);
    }

    @Test
    void NotebookService_UpdateNotebook_SameName_NotebookIsUpdatedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        UUID notebookId = UUID.randomUUID();
        Notebook notebook = new Notebook();
        notebook.setId(notebookId);
        notebook.setName("Same Name");
        notebook.setOwner(user);
        notebook.setNotes(new ArrayList<>());

        NotebookRequest request = new NotebookRequest("Same Name");

        when(notebookRepository.findById(notebookId)).thenReturn(Optional.of(notebook));
        when(notebookRepository.save(Mockito.any(Notebook.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        NotebookResponse response = notebookService.updateNotebook(notebookId, request);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getName()).isEqualTo("Same Name");
        Mockito.verify(notebookRepository).findById(notebookId);
        Mockito.verify(notebookRepository, Mockito.never()).existsByNameAndOwnerId(Mockito.any(), Mockito.any());
        Mockito.verify(notebookRepository).save(notebook);
    }

    @Test
    void NotebookService_UpdateNotebook_NotebookNotFound_ThrowsNotebookNotFoundException() {
        // Arrange
        mockAuthenticatedUser(user);
        UUID notebookId = UUID.randomUUID();
        NotebookRequest request = new NotebookRequest("New Name");

        when(notebookRepository.findById(notebookId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.updateNotebook(notebookId, request))
                .isInstanceOf(NotebookNotFoundException.class);
        Mockito.verify(notebookRepository).findById(notebookId);
        Mockito.verify(notebookRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void NotebookService_UpdateNotebook_UnauthorizedAccess_ThrowsUnauthorizedNotebookAccessException() {
        // Arrange
        mockAuthenticatedUser(user);

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        UUID notebookId = UUID.randomUUID();
        Notebook notebook = new Notebook();
        notebook.setId(notebookId);
        notebook.setName("Old Name");
        notebook.setOwner(anotherUser);

        NotebookRequest request = new NotebookRequest("New Name");

        when(notebookRepository.findById(notebookId)).thenReturn(Optional.of(notebook));

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.updateNotebook(notebookId, request))
                .isInstanceOf(UnauthorizedNotebookAccessException.class);
        Mockito.verify(notebookRepository).findById(notebookId);
        Mockito.verify(notebookRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void NotebookService_UpdateNotebook_DuplicateName_ThrowsDuplicateNotebookException() {
        // Arrange
        mockAuthenticatedUser(user);

        UUID notebookId = UUID.randomUUID();
        Notebook notebook = new Notebook();
        notebook.setId(notebookId);
        notebook.setName("Old Name");
        notebook.setOwner(user);

        NotebookRequest request = new NotebookRequest("Existing Name");

        when(notebookRepository.findById(notebookId)).thenReturn(Optional.of(notebook));
        when(notebookRepository.existsByNameAndOwnerId("Existing Name", userId)).thenReturn(true);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.updateNotebook(notebookId, request))
                .isInstanceOf(DuplicateNotebookException.class);
        Mockito.verify(notebookRepository).findById(notebookId);
        Mockito.verify(notebookRepository).existsByNameAndOwnerId("Existing Name", userId);
        Mockito.verify(notebookRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void NotebookService_UpdateNotebook_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
        // Arrange
        SecurityContextHolder.clearContext();
        UUID notebookId = UUID.randomUUID();
        NotebookRequest request = new NotebookRequest("New Name");

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.updateNotebook(notebookId, request))
                .isInstanceOf(UserNotAuthenticatedException.class);
        Mockito.verify(notebookRepository, Mockito.never()).findById(Mockito.any());
    }

    // ==================== deleteNotebook ====================

    @Test
    void NotebookService_DeleteNotebook_NotebookIsDeletedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        UUID notebookId = UUID.randomUUID();
        Notebook notebook = new Notebook();
        notebook.setId(notebookId);
        notebook.setOwner(user);

        when(notebookRepository.findById(notebookId)).thenReturn(Optional.of(notebook));

        // Act
        notebookService.deleteNotebook(notebookId);

        // Assert
        Mockito.verify(notebookRepository).findById(notebookId);
        Mockito.verify(notebookRepository).delete(notebook);
    }

    @Test
    void NotebookService_DeleteNotebook_NotebookNotFound_ThrowsNotebookNotFoundException() {
        // Arrange
        mockAuthenticatedUser(user);
        UUID notebookId = UUID.randomUUID();

        when(notebookRepository.findById(notebookId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.deleteNotebook(notebookId))
                .isInstanceOf(NotebookNotFoundException.class);
        Mockito.verify(notebookRepository).findById(notebookId);
        Mockito.verify(notebookRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    void NotebookService_DeleteNotebook_UnauthorizedAccess_ThrowsUnauthorizedNotebookAccessException() {
        // Arrange
        mockAuthenticatedUser(user);

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        UUID notebookId = UUID.randomUUID();
        Notebook notebook = new Notebook();
        notebook.setId(notebookId);
        notebook.setOwner(anotherUser);

        when(notebookRepository.findById(notebookId)).thenReturn(Optional.of(notebook));

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.deleteNotebook(notebookId))
                .isInstanceOf(UnauthorizedNotebookAccessException.class);
        Mockito.verify(notebookRepository).findById(notebookId);
        Mockito.verify(notebookRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    void NotebookService_DeleteNotebook_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
        // Arrange
        SecurityContextHolder.clearContext();
        UUID notebookId = UUID.randomUUID();

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.deleteNotebook(notebookId))
                .isInstanceOf(UserNotAuthenticatedException.class);
        Mockito.verify(notebookRepository, Mockito.never()).findById(Mockito.any());
    }

    // ==================== countNotebooks ====================

    @Test
    void NotebookService_CountNotebooks_NotebooksAreCountedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);
        when(notebookRepository.countByOwnerId(userId)).thenReturn(5L);

        // Act
        long count = notebookService.countNotebooks();

        // Assert
        Assertions.assertThat(count).isEqualTo(5L);
        Mockito.verify(notebookRepository).countByOwnerId(userId);
    }

    @Test
    void NotebookService_CountNotebooks_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
        // Arrange
        SecurityContextHolder.clearContext();

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.countNotebooks())
                .isInstanceOf(UserNotAuthenticatedException.class);
        Mockito.verify(notebookRepository, Mockito.never()).countByOwnerId(Mockito.any());
    }

    // ==================== createNoteInNotebook ====================

    @Test
    void NotebookService_CreateNoteInNotebook_NoteIsCreatedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        UUID notebookId = UUID.randomUUID();
        Notebook notebook = new Notebook();
        notebook.setId(notebookId);
        notebook.setName("Work");
        notebook.setOwner(user);
        notebook.setNotes(new ArrayList<>());

        NoteRequest noteRequest = new NoteRequest();
        noteRequest.setTitle("Chapter 1");
        noteRequest.setDescription("Content of chapter 1");

        UUID noteId = UUID.randomUUID();
        Note savedNote = new Note();
        savedNote.setId(noteId);
        savedNote.setTitle("Chapter 1");
        savedNote.setDescription("Content of chapter 1");
        savedNote.setOwner(user);
        savedNote.setNotebook(notebook);

        when(notebookRepository.findById(notebookId)).thenReturn(Optional.of(notebook));
        when(noteRepository.save(Mockito.any(Note.class))).thenReturn(savedNote);

        // Act
        NoteResponse response = notebookService.createNoteInNotebook(notebookId, noteRequest);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(noteId);
        Assertions.assertThat(response.getTitle()).isEqualTo("Chapter 1");
        Assertions.assertThat(response.getDescription()).isEqualTo("Content of chapter 1");
        Assertions.assertThat(notebook.getNotes()).contains(savedNote);
        Mockito.verify(notebookRepository).findById(notebookId);
        Mockito.verify(noteRepository).save(Mockito.any(Note.class));
    }

    @Test
    void NotebookService_CreateNoteInNotebook_NotebookNotFound_ThrowsNotebookNotFoundException() {
        // Arrange
        mockAuthenticatedUser(user);
        UUID notebookId = UUID.randomUUID();
        NoteRequest noteRequest = new NoteRequest();
        noteRequest.setTitle("Chapter 1");

        when(notebookRepository.findById(notebookId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.createNoteInNotebook(notebookId, noteRequest))
                .isInstanceOf(NotebookNotFoundException.class);
        Mockito.verify(notebookRepository).findById(notebookId);
        Mockito.verify(noteRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void NotebookService_CreateNoteInNotebook_UnauthorizedAccess_ThrowsUnauthorizedNotebookAccessException() {
        // Arrange
        mockAuthenticatedUser(user);

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        UUID notebookId = UUID.randomUUID();
        Notebook notebook = new Notebook();
        notebook.setId(notebookId);
        notebook.setName("Work");
        notebook.setOwner(anotherUser);

        NoteRequest noteRequest = new NoteRequest();
        noteRequest.setTitle("Chapter 1");

        when(notebookRepository.findById(notebookId)).thenReturn(Optional.of(notebook));

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.createNoteInNotebook(notebookId, noteRequest))
                .isInstanceOf(UnauthorizedNotebookAccessException.class);
        Mockito.verify(notebookRepository).findById(notebookId);
        Mockito.verify(noteRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void NotebookService_CreateNoteInNotebook_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
        // Arrange
        SecurityContextHolder.clearContext();
        UUID notebookId = UUID.randomUUID();
        NoteRequest noteRequest = new NoteRequest();

        // Act & Assert
        Assertions.assertThatThrownBy(() -> notebookService.createNoteInNotebook(notebookId, noteRequest))
                .isInstanceOf(UserNotAuthenticatedException.class);
        Mockito.verify(notebookRepository, Mockito.never()).findById(Mockito.any());
    }
}
