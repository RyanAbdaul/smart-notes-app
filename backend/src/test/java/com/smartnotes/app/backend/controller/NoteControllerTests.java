package com.smartnotes.app.backend.controller;

import com.smartnotes.app.backend.request.NoteRequest;
import com.smartnotes.app.backend.request.UpdateNoteRequest;
import com.smartnotes.app.backend.response.NoteResponse;
import com.smartnotes.app.backend.rest.NoteController;
import com.smartnotes.app.backend.service.NoteService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteControllerTests {

    @Mock
    private NoteService noteService;

    @InjectMocks
    private NoteController noteController;

    // ==================== createNote Endpoint ====================
//
//    @Test
//    void NoteController_CreateNote_NoteIsCreatedSuccessfully() {
//        // Arrange
//        NoteRequest request = new NoteRequest();
//        request.setTitle("Meeting Notes");
//        request.setDescription("Discussion about project roadmap");
//
//        UUID noteId = UUID.randomUUID();
//        NoteResponse expectedResponse = new NoteResponse();
//        expectedResponse.setId(noteId);
//        expectedResponse.setTitle("Meeting Notes");
//        expectedResponse.setDescription("Discussion about project roadmap");
//
//        when(noteService.createNote(request)).thenReturn(expectedResponse);
//
//        // Act
//        NoteResponse actualResponse = noteController.createNote(request);
//
//        // Assert
//        Assertions.assertThat(actualResponse).isNotNull();
//        Assertions.assertThat(actualResponse.getId()).isEqualTo(noteId);
//        Assertions.assertThat(actualResponse.getTitle()).isEqualTo("Meeting Notes");
//        Assertions.assertThat(actualResponse.getDescription()).isEqualTo("Discussion about project roadmap");
//        verify(noteService).createNote(request);
//    }

    // ==================== getNoteById Endpoint ====================

    @Test
    void NoteController_GetNoteById_NoteIsReturnedSuccessfully() {
        // Arrange
        UUID noteId = UUID.randomUUID();
        NoteResponse expectedResponse = new NoteResponse();
        expectedResponse.setId(noteId);
        expectedResponse.setTitle("Personal Note");
        expectedResponse.setDescription("Groceries to buy");

        when(noteService.getNoteById(noteId)).thenReturn(expectedResponse);

        // Act
        NoteResponse actualResponse = noteController.getNoteById(noteId);

        // Assert
        Assertions.assertThat(actualResponse).isNotNull();
        Assertions.assertThat(actualResponse.getId()).isEqualTo(noteId);
        Assertions.assertThat(actualResponse.getTitle()).isEqualTo("Personal Note");
        Assertions.assertThat(actualResponse.getDescription()).isEqualTo("Groceries to buy");
        verify(noteService).getNoteById(noteId);
    }

    // ==================== getAllNotes Endpoint ====================

//    @Test
//    void NoteController_GetAllNotes_NotesListIsReturnedSuccessfully() {
//        // Arrange
//        List<NoteResponse> expectedList = new ArrayList<>();
//
//        NoteResponse note1 = new NoteResponse();
//        note1.setId(UUID.randomUUID());
//        note1.setTitle("First Note");
//
//        NoteResponse note2 = new NoteResponse();
//        note2.setId(UUID.randomUUID());
//        note2.setTitle("Second Note");
//
//        expectedList.add(note1);
//        expectedList.add(note2);
//
//        when(noteService.getAllNotes()).thenReturn(expectedList);
//
//        // Act
//        List<NoteResponse> actualList = noteController.getAllNotes();
//
//        // Assert
//        Assertions.assertThat(actualList).isNotNull();
//        Assertions.assertThat(actualList).hasSize(2);
//        Assertions.assertThat(actualList.get(0).getTitle()).isEqualTo("First Note");
//        Assertions.assertThat(actualList.get(1).getTitle()).isEqualTo("Second Note");
//        verify(noteService).getAllNotes();
//    }

    // ==================== updateNote Endpoint ====================

    @Test
    void NoteController_UpdateNote_NoteIsUpdatedSuccessfully() {
        // Arrange
        UUID noteId = UUID.randomUUID();
        UpdateNoteRequest request = new UpdateNoteRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");

        NoteResponse expectedResponse = new NoteResponse();
        expectedResponse.setId(noteId);
        expectedResponse.setTitle("Updated Title");
        expectedResponse.setDescription("Updated Description");

        when(noteService.updateNote(noteId, request)).thenReturn(expectedResponse);

        // Act
        NoteResponse actualResponse = noteController.updateNote(noteId, request);

        // Assert
        Assertions.assertThat(actualResponse).isNotNull();
        Assertions.assertThat(actualResponse.getId()).isEqualTo(noteId);
        Assertions.assertThat(actualResponse.getTitle()).isEqualTo("Updated Title");
        Assertions.assertThat(actualResponse.getDescription()).isEqualTo("Updated Description");
        verify(noteService).updateNote(noteId, request);
    }

    // ==================== pinNote Endpoint ====================

    @Test
    void NoteController_PinNote_NoteIsPinnedSuccessfully() {
        // Arrange
        UUID noteId = UUID.randomUUID();

        // Act
        noteController.pinNote(noteId);

        // Assert
        verify(noteService).pinNote(noteId);
    }

    // ==================== deleteNote Endpoint ====================

//    @Test
//    void NoteController_DeleteNote_NoteIsDeletedSuccessfully() {
//        // Arrange
//        UUID noteId = UUID.randomUUID();
//
//        // Act
//        noteController.deleteNote(noteId);
//
//        // Assert
//        verify(noteService).deleteNote(noteId);
//    }

    // ==================== countNotes Endpoint ====================

    @Test
    void NoteController_CountNotes_CountIsReturnedSuccessfully() {
        // Arrange
        when(noteService.countNotes()).thenReturn(10L);

        // Act
        long count = noteController.countNotes();

        // Assert
        Assertions.assertThat(count).isEqualTo(10L);
        verify(noteService).countNotes();
    }
}
