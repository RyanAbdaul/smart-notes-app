package com.smartnotes.app.backend.controller;

import com.smartnotes.app.backend.request.TagRequest;
import com.smartnotes.app.backend.response.TagResponse;
import com.smartnotes.app.backend.rest.TagController;
import com.smartnotes.app.backend.service.TagService;
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
class TagControllerTests {

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    // ==================== create Endpoint ====================
//
//    @Test
//    void TagController_Create_TagIsCreatedSuccessfully() {
//        // Arrange
//        TagRequest request = new TagRequest("finance");
//
//        UUID tagId = UUID.randomUUID();
//        TagResponse expectedResponse = new TagResponse();
//        expectedResponse.setId(tagId);
//        expectedResponse.setName("finance");
//
//        when(tagService.create(request)).thenReturn(expectedResponse);
//
//        // Act
//        TagResponse actualResponse = tagController.create(request);
//
//        // Assert
//        Assertions.assertThat(actualResponse).isNotNull();
//        Assertions.assertThat(actualResponse.getId()).isEqualTo(tagId);
//        Assertions.assertThat(actualResponse.getName()).isEqualTo("finance");
//        verify(tagService).create(request);
//    }

    // ==================== getAll Endpoint ====================

    @Test
    void TagController_GetAll_TagsListIsReturnedSuccessfully() {
        // Arrange
        List<TagResponse> expectedList = new ArrayList<>();

        TagResponse tag1 = new TagResponse();
        tag1.setId(UUID.randomUUID());
        tag1.setName("work");

        TagResponse tag2 = new TagResponse();
        tag2.setId(UUID.randomUUID());
        tag2.setName("personal");

        expectedList.add(tag1);
        expectedList.add(tag2);

        when(tagService.getAll()).thenReturn(expectedList);

        // Act
        List<TagResponse> actualList = tagController.getAll();

        // Assert
        Assertions.assertThat(actualList).isNotNull();
        Assertions.assertThat(actualList).hasSize(2);
        Assertions.assertThat(actualList.get(0).getName()).isEqualTo("work");
        Assertions.assertThat(actualList.get(1).getName()).isEqualTo("personal");
        verify(tagService).getAll();
    }

    // ==================== getById Endpoint ====================

    @Test
    void TagController_GetById_TagIsReturnedSuccessfully() {
        // Arrange
        UUID tagId = UUID.randomUUID();
        TagResponse expectedResponse = new TagResponse();
        expectedResponse.setId(tagId);
        expectedResponse.setName("recipes");

        when(tagService.getById(tagId)).thenReturn(expectedResponse);

        // Act
        TagResponse actualResponse = tagController.getById(tagId);

        // Assert
        Assertions.assertThat(actualResponse).isNotNull();
        Assertions.assertThat(actualResponse.getId()).isEqualTo(tagId);
        Assertions.assertThat(actualResponse.getName()).isEqualTo("recipes");
        verify(tagService).getById(tagId);
    }

    // ==================== update Endpoint ====================

    @Test
    void TagController_Update_TagIsUpdatedSuccessfully() {
        // Arrange
        UUID tagId = UUID.randomUUID();
        TagRequest request = new TagRequest("updated-tag");

        TagResponse expectedResponse = new TagResponse();
        expectedResponse.setId(tagId);
        expectedResponse.setName("updated-tag");

        when(tagService.update(tagId, request)).thenReturn(expectedResponse);

        // Act
        TagResponse actualResponse = tagController.update(tagId, request);

        // Assert
        Assertions.assertThat(actualResponse).isNotNull();
        Assertions.assertThat(actualResponse.getId()).isEqualTo(tagId);
        Assertions.assertThat(actualResponse.getName()).isEqualTo("updated-tag");
        verify(tagService).update(tagId, request);
    }

    // ==================== count Endpoint ====================

    @Test
    void TagController_Count_CountIsReturnedSuccessfully() {
        // Arrange
        when(tagService.tagsCount()).thenReturn(15L);

        // Act
        Long actualCount = tagController.count();

        // Assert
        Assertions.assertThat(actualCount).isEqualTo(15L);
        verify(tagService).tagsCount();
    }

    // ==================== delete Endpoint ====================

    @Test
    void TagController_Delete_TagIsDeletedSuccessfully() {
        // Arrange
        UUID tagId = UUID.randomUUID();

        // Act
        tagController.delete(tagId);

        // Assert
        verify(tagService).delete(tagId);
    }
}
