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
class NoteServiceTests {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
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

    // ==================== createNote ====================

    @Test
    void NoteService_CreateNote_NoteIsCreatedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        NoteRequest noteRequest = new NoteRequest();
        noteRequest.setTitle("Note Title");
        noteRequest.setDescription("Note Description");

        UUID noteId = UUID.randomUUID();
        Note savedNote = new Note();
        savedNote.setId(noteId);
        savedNote.setTitle(noteRequest.getTitle());
        savedNote.setDescription(noteRequest.getDescription());
        savedNote.setPinned(false);
        savedNote.setOwner(user);

        when(noteRepository.save(Mockito.any(Note.class))).thenReturn(savedNote);

        // Act
        NoteResponse response = noteService.createNote(noteRequest);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(noteId);
        Assertions.assertThat(response.getTitle()).isEqualTo("Note Title");
        Assertions.assertThat(response.getDescription()).isEqualTo("Note Description");
        Assertions.assertThat(response.isPinned()).isFalse();
        Mockito.verify(noteRepository).save(Mockito.any(Note.class));
    }

    @Test
    void NoteService_CreateNote_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
        // Arrange
        SecurityContextHolder.clearContext();
        NoteRequest noteRequest = new NoteRequest();
        noteRequest.setTitle("Note Title");
        noteRequest.setDescription("Note Description");

        // Act & Assert
        Assertions.assertThatThrownBy(() -> noteService.createNote(noteRequest))
                .isInstanceOf(UserNotAuthenticatedException.class)
                .hasMessageContaining("User not authenticated");
        Mockito.verify(noteRepository, Mockito.never()).save(Mockito.any(Note.class));
    }

    // ==================== getNoteById ====================

    @Test
    void NoteService_GetNoteById_NoteIsReturnedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        UUID noteId = UUID.randomUUID();
        Note note = new Note();
        note.setId(noteId);
        note.setTitle("Test Note");
        note.setDescription("Test Description");
        note.setOwner(user);

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        // Act
        NoteResponse response = noteService.getNoteById(noteId);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(noteId);
        Assertions.assertThat(response.getTitle()).isEqualTo("Test Note");
        Assertions.assertThat(response.getDescription()).isEqualTo("Test Description");
        Mockito.verify(noteRepository).findById(noteId);
    }

    @Test
    void NoteService_GetNoteById_NoteNotFound_ThrowsNoteNotFoundException() {
        // Arrange
        mockAuthenticatedUser(user);
        UUID noteId = UUID.randomUUID();
        when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> noteService.getNoteById(noteId))
                .isInstanceOf(NoteNotFoundException.class);
        Mockito.verify(noteRepository).findById(noteId);
    }

    @Test
    void NoteService_GetNoteById_UnauthorizedAccess_ThrowsUnauthorizedNoteAccessException() {
        // Arrange
        mockAuthenticatedUser(user);

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        UUID noteId = UUID.randomUUID();
        Note note = new Note();
        note.setId(noteId);
        note.setOwner(anotherUser);

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        // Act & Assert
        Assertions.assertThatThrownBy(() -> noteService.getNoteById(noteId))
                .isInstanceOf(UnauthorizedNoteAccessException.class);
        Mockito.verify(noteRepository).findById(noteId);
    }

    @Test
    void NoteService_GetNoteById_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
        // Arrange
        SecurityContextHolder.clearContext();
        UUID noteId = UUID.randomUUID();

        // Act & Assert
        Assertions.assertThatThrownBy(() -> noteService.getNoteById(noteId))
                .isInstanceOf(UserNotAuthenticatedException.class);
        Mockito.verify(noteRepository, Mockito.never()).findById(Mockito.any());
    }

    // ==================== getAllNotes ====================
//
//    @Test
//    void NoteService_GetAllNotes_NotesAreReturnedSuccessfully() {
//        // Arrange
//        mockAuthenticatedUser(user);
//
//        User anotherUser = new User();
//        anotherUser.setId(UUID.randomUUID());
//
//        Note note1 = new Note();
//        note1.setId(UUID.randomUUID());
//        note1.setTitle("Note 1");
//        note1.setOwner(user);
//        note1.setPinned(true);
//
//        Note note2 = new Note();
//        note2.setId(UUID.randomUUID());
//        note2.setTitle("Note 2");
//        note2.setOwner(user);
//        note2.setPinned(false);
//
//        Note note3 = new Note();
//        note3.setId(UUID.randomUUID());
//        note3.setTitle("Note 3");
//        note3.setOwner(anotherUser); // should be filtered out
//        note3.setPinned(false);
//
//        List<Note> notes = List.of(note1, note2, note3);
//        when(noteRepository.findAll()).thenReturn(notes);
//
//        // Act
//        List<NoteResponse> result = noteService.getAllNotes();
//
//        // Assert
//        Assertions.assertThat(result).hasSize(2);
//        Assertions.assertThat(result.get(0).getId()).isEqualTo(note1.getId());
//        Assertions.assertThat(result.get(0).isPinned()).isTrue();
//        Assertions.assertThat(result.get(1).getId()).isEqualTo(note2.getId());
//        Assertions.assertThat(result.get(1).isPinned()).isFalse();
//        Mockito.verify(noteRepository).findNotes();
//    }
//
//    @Test
//    void NoteService_GetAllNotes_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
//        // Arrange
//        SecurityContextHolder.clearContext();
//
//        // Act & Assert
//        Assertions.assertThatThrownBy(() -> noteService.getAllNotes())
//                .isInstanceOf(UserNotAuthenticatedException.class);
//        Mockito.verify(noteRepository, Mockito.never()).findAllByOrderByIsPinnedDesc();
//    }

    // ==================== updateNote ====================

    @Test
    void NoteService_UpdateNote_NoteIsUpdatedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        UUID noteId = UUID.randomUUID();
        Note note = new Note();
        note.setId(noteId);
        note.setTitle("Old Title");
        note.setDescription("Old Description");
        note.setImageUrl("old-image.png");
        note.setOwner(user);

        UpdateNoteRequest request = new UpdateNoteRequest();
        request.setTitle("New Title");
        request.setDescription("New Description");
        request.setImageUrl("new-image.png");

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));
        when(noteRepository.save(Mockito.any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        NoteResponse result = noteService.updateNote(noteId, request);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTitle()).isEqualTo("New Title");
        Assertions.assertThat(result.getDescription()).isEqualTo("New Description");
        Assertions.assertThat(result.getImageUrl()).isEqualTo("new-image.png");
        Mockito.verify(noteRepository).findById(noteId);
        Mockito.verify(noteRepository).save(note);
    }

    @Test
    void NoteService_UpdateNote_NoteNotFound_ThrowsNoteNotFoundException() {
        // Arrange
        mockAuthenticatedUser(user);
        UUID noteId = UUID.randomUUID();
        UpdateNoteRequest request = new UpdateNoteRequest();
        request.setTitle("Updated Title");

        when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> noteService.updateNote(noteId, request))
                .isInstanceOf(NoteNotFoundException.class);
        Mockito.verify(noteRepository).findById(noteId);
        Mockito.verify(noteRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void NoteService_UpdateNote_UnauthorizedAccess_ThrowsUnauthorizedNoteAccessException() {
        // Arrange
        mockAuthenticatedUser(user);

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        UUID noteId = UUID.randomUUID();
        Note note = new Note();
        note.setId(noteId);
        note.setOwner(anotherUser);

        UpdateNoteRequest request = new UpdateNoteRequest();

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        // Act & Assert
        Assertions.assertThatThrownBy(() -> noteService.updateNote(noteId, request))
                .isInstanceOf(UnauthorizedNoteAccessException.class);
        Mockito.verify(noteRepository).findById(noteId);
        Mockito.verify(noteRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void NoteService_UpdateNote_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
        // Arrange
        SecurityContextHolder.clearContext();
        UUID noteId = UUID.randomUUID();
        UpdateNoteRequest request = new UpdateNoteRequest();

        // Act & Assert
        Assertions.assertThatThrownBy(() -> noteService.updateNote(noteId, request))
                .isInstanceOf(UserNotAuthenticatedException.class);
        Mockito.verify(noteRepository, Mockito.never()).findById(Mockito.any());
    }

    // ==================== deleteNote ====================

    @Test
    void NoteService_DeleteNote_NoteIsDeletedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        UUID noteId = UUID.randomUUID();
        Note note = new Note();
        note.setId(noteId);
        note.setOwner(user);

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        // Act
        noteService.deleteNote(noteId);

        // Assert
        Mockito.verify(noteRepository).findById(noteId);
        Mockito.verify(noteRepository).delete(note);
    }

    @Test
    void NoteService_DeleteNote_NoteNotFound_ThrowsNoteNotFoundException() {
        // Arrange
        mockAuthenticatedUser(user);
        UUID noteId = UUID.randomUUID();

        when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> noteService.deleteNote(noteId))
                .isInstanceOf(NoteNotFoundException.class);
        Mockito.verify(noteRepository).findById(noteId);
        Mockito.verify(noteRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    void NoteService_DeleteNote_UnauthorizedAccess_ThrowsUnauthorizedNoteAccessException() {
        // Arrange
        mockAuthenticatedUser(user);

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        UUID noteId = UUID.randomUUID();
        Note note = new Note();
        note.setId(noteId);
        note.setOwner(anotherUser);

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        // Act & Assert
        Assertions.assertThatThrownBy(() -> noteService.deleteNote(noteId))
                .isInstanceOf(UnauthorizedNoteAccessException.class);
        Mockito.verify(noteRepository).findById(noteId);
        Mockito.verify(noteRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    void NoteService_DeleteNote_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
        // Arrange
        SecurityContextHolder.clearContext();
        UUID noteId = UUID.randomUUID();

        // Act & Assert
        Assertions.assertThatThrownBy(() -> noteService.deleteNote(noteId))
                .isInstanceOf(UserNotAuthenticatedException.class);
        Mockito.verify(noteRepository, Mockito.never()).findById(Mockito.any());
    }

    // ==================== countNotes ====================

    @Test
    void NoteService_CountNotes_NotesAreCountedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        Note note1 = new Note();
        note1.setOwner(user);

        Note note2 = new Note();
        note2.setOwner(user);

        Note note3 = new Note();
        note3.setOwner(anotherUser);

        when(noteRepository.findAll()).thenReturn(List.of(note1, note2, note3));

        // Act
        long count = noteService.countNotes();

        // Assert
        Assertions.assertThat(count).isEqualTo(2);
        Mockito.verify(noteRepository).findAll();
    }

    @Test
    void NoteService_CountNotes_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
        // Arrange
        SecurityContextHolder.clearContext();

        // Act & Assert
        Assertions.assertThatThrownBy(() -> noteService.countNotes())
                .isInstanceOf(UserNotAuthenticatedException.class);
        Mockito.verify(noteRepository, Mockito.never()).findAll();
    }

    // ==================== pinNote ====================

    @Test
    void NoteService_PinNote_NoteIsPinnedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        UUID noteId = UUID.randomUUID();
        Note note = new Note();
        note.setId(noteId);
        note.setOwner(user);
        note.setPinned(false);

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));
        when(noteRepository.save(Mockito.any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        noteService.pinNote(noteId);

        // Assert
        Assertions.assertThat(note.isPinned()).isTrue();
        Mockito.verify(noteRepository).findById(noteId);
        Mockito.verify(noteRepository).save(note);
    }

    @Test
    void NoteService_PinNote_UnpinNoteSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        UUID noteId = UUID.randomUUID();
        Note note = new Note();
        note.setId(noteId);
        note.setOwner(user);
        note.setPinned(true);

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));
        when(noteRepository.save(Mockito.any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        noteService.pinNote(noteId);

        // Assert
        Assertions.assertThat(note.isPinned()).isFalse();
        Mockito.verify(noteRepository).findById(noteId);
        Mockito.verify(noteRepository).save(note);
    }

    @Test
    void NoteService_PinNote_NoteNotFound_ThrowsNoteNotFoundException() {
        // Arrange
        mockAuthenticatedUser(user);
        UUID noteId = UUID.randomUUID();

        when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> noteService.pinNote(noteId))
                .isInstanceOf(NoteNotFoundException.class);
        Mockito.verify(noteRepository).findById(noteId);
        Mockito.verify(noteRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void NoteService_PinNote_UnauthorizedAccess_ThrowsUnauthorizedNoteAccessException() {
        // Arrange
        mockAuthenticatedUser(user);

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        UUID noteId = UUID.randomUUID();
        Note note = new Note();
        note.setId(noteId);
        note.setOwner(anotherUser);

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        // Act & Assert
        Assertions.assertThatThrownBy(() -> noteService.pinNote(noteId))
                .isInstanceOf(UnauthorizedNoteAccessException.class);
        Mockito.verify(noteRepository).findById(noteId);
        Mockito.verify(noteRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void NoteService_PinNote_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
        // Arrange
        SecurityContextHolder.clearContext();
        UUID noteId = UUID.randomUUID();

        // Act & Assert
        Assertions.assertThatThrownBy(() -> noteService.pinNote(noteId))
                .isInstanceOf(UserNotAuthenticatedException.class);
        Mockito.verify(noteRepository, Mockito.never()).findById(Mockito.any());
    }
}
