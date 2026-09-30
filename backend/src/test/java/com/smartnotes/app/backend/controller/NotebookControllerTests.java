package com.smartnotes.app.backend.controller;

import com.smartnotes.app.backend.request.NoteRequest;
import com.smartnotes.app.backend.request.NotebookRequest;
import com.smartnotes.app.backend.response.NoteResponse;
import com.smartnotes.app.backend.response.NotebookResponse;
import com.smartnotes.app.backend.rest.NotebookController;
import com.smartnotes.app.backend.service.NotebookService;
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
class NotebookControllerTests {

    @Mock
    private NotebookService notebookService;

    @InjectMocks
    private NotebookController notebookController;

    // ==================== createNotebook Endpoint ====================

    @Test
    void NotebookController_CreateNotebook_NotebookIsCreatedSuccessfully() {
        // Arrange
        NotebookRequest request = new NotebookRequest("Work Projects");

        UUID notebookId = UUID.randomUUID();
        NotebookResponse expectedResponse = new NotebookResponse();
        expectedResponse.setId(notebookId);
        expectedResponse.setName("Work Projects");

        when(notebookService.createNotebook(request)).thenReturn(expectedResponse);

        // Act
        NotebookResponse actualResponse = notebookController.createNotebook(request);

        // Assert
        Assertions.assertThat(actualResponse).isNotNull();
        Assertions.assertThat(actualResponse.getId()).isEqualTo(notebookId);
        Assertions.assertThat(actualResponse.getName()).isEqualTo("Work Projects");
        verify(notebookService).createNotebook(request);
    }

    // ==================== createNoteInNotebook Endpoint ====================

//    @Test
//    void NotebookController_CreateNoteInNotebook_NoteIsCreatedSuccessfully() {
//        // Arrange
//        UUID notebookId = UUID.randomUUID();
//        NoteRequest request = new NoteRequest();
//        request.setTitle("Chapter 1 Summary");
//        request.setDescription("Details about chapter 1");
//
//        UUID noteId = UUID.randomUUID();
//        NoteResponse expectedResponse = new NoteResponse();
//        expectedResponse.setId(noteId);
//        expectedResponse.setTitle("Chapter 1 Summary");
//        expectedResponse.setDescription("Details about chapter 1");
//
//        when(notebookService.createNoteInNotebook(notebookId, request)).thenReturn(expectedResponse);
//
//        // Act
//        NoteResponse actualResponse = notebookController.createNoteInNotebook(notebookId, request);
//
//        // Assert
//        Assertions.assertThat(actualResponse).isNotNull();
//        Assertions.assertThat(actualResponse.getId()).isEqualTo(noteId);
//        Assertions.assertThat(actualResponse.getTitle()).isEqualTo("Chapter 1 Summary");
//        Assertions.assertThat(actualResponse.getDescription()).isEqualTo("Details about chapter 1");
//        verify(notebookService).createNoteInNotebook(notebookId, request);
//    }

    // ==================== getNotebookById Endpoint ====================

    @Test
    void NotebookController_GetNotebookById_NotebookIsReturnedSuccessfully() {
        // Arrange
        UUID notebookId = UUID.randomUUID();
        NotebookResponse expectedResponse = new NotebookResponse();
        expectedResponse.setId(notebookId);
        expectedResponse.setName("Personal Notes");

        when(notebookService.getNotebookById(notebookId)).thenReturn(expectedResponse);

        // Act
        NotebookResponse actualResponse = notebookController.getNotebookById(notebookId);

        // Assert
        Assertions.assertThat(actualResponse).isNotNull();
        Assertions.assertThat(actualResponse.getId()).isEqualTo(notebookId);
        Assertions.assertThat(actualResponse.getName()).isEqualTo("Personal Notes");
        verify(notebookService).getNotebookById(notebookId);
    }

    // ==================== getAllNotebooks Endpoint ====================

    @Test
    void NotebookController_GetAllNotebooks_NotebooksListIsReturnedSuccessfully() {
        // Arrange
        List<NotebookResponse> expectedList = new ArrayList<>();

        NotebookResponse notebook1 = new NotebookResponse();
        notebook1.setId(UUID.randomUUID());
        notebook1.setName("Notebook One");

        NotebookResponse notebook2 = new NotebookResponse();
        notebook2.setId(UUID.randomUUID());
        notebook2.setName("Notebook Two");

        expectedList.add(notebook1);
        expectedList.add(notebook2);

        when(notebookService.getAllNotebooks()).thenReturn(expectedList);

        // Act
        List<NotebookResponse> actualList = notebookController.getAllNotebooks();

        // Assert
        Assertions.assertThat(actualList).isNotNull();
        Assertions.assertThat(actualList).hasSize(2);
        Assertions.assertThat(actualList.get(0).getName()).isEqualTo("Notebook One");
        Assertions.assertThat(actualList.get(1).getName()).isEqualTo("Notebook Two");
        verify(notebookService).getAllNotebooks();
    }

    // ==================== updateNotebook Endpoint ====================

//    @Test
//    void NotebookController_UpdateNotebook_NotebookIsUpdatedSuccessfully() {
//        // Arrange
//        UUID notebookId = UUID.randomUUID();
//        NotebookRequest request = new NotebookRequest("Updated Notebook Name");
//
//        NotebookResponse expectedResponse = new NotebookResponse();
//        expectedResponse.setId(notebookId);
//        expectedResponse.setName("Updated Notebook Name");
//
//        when(notebookService.updateNotebook(notebookId, request)).thenReturn(expectedResponse);
//
//        // Act
//        NotebookResponse actualResponse = notebookController.updateNotebook(notebookId, request);
//
//        // Assert
//        Assertions.assertThat(actualResponse).isNotNull();
//        Assertions.assertThat(actualResponse.getId()).isEqualTo(notebookId);
//        Assertions.assertThat(actualResponse.getName()).isEqualTo("Updated Notebook Name");
//        verify(notebookService).updateNotebook(notebookId, request);
//    }

    // ==================== deleteNotebook Endpoint ====================
//
//    @Test
//    void NotebookController_DeleteNotebook_NotebookIsDeletedSuccessfully() {
//        // Arrange
//        UUID notebookId = UUID.randomUUID();
//
//        // Act
//        notebookController.deleteNotebook(notebookId);
//
//        // Assert
//        verify(notebookService).deleteNotebook(notebookId);
//    }

    // ==================== countNotebooks Endpoint ====================

    @Test
    void NotebookController_CountNotebooks_CountIsReturnedSuccessfully() {
        // Arrange
        when(notebookService.countNotebooks()).thenReturn(7L);

        // Act
        long count = notebookController.countNotebooks();

        // Assert
        Assertions.assertThat(count).isEqualTo(7L);
        verify(notebookService).countNotebooks();
    }
}
