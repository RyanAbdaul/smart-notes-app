package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.entity.Tag;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.exception.DuplicateTagException;
import com.smartnotes.app.backend.exception.TagNotFoundException;
import com.smartnotes.app.backend.exception.UserNotAuthenticatedException;
import com.smartnotes.app.backend.repository.TagRepository;
import com.smartnotes.app.backend.request.TagRequest;
import com.smartnotes.app.backend.response.TagResponse;
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
class TagServiceTests {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setFirstName("Bob");
        user.setLastName("Builder");
        user.setEmail("bob@example.com");
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

    // ==================== create ====================

    @Test
    void TagService_Create_TagIsCreatedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        TagRequest request = new TagRequest("important");
        when(tagRepository.existsByName("important")).thenReturn(false);

        UUID tagId = UUID.randomUUID();
        Tag savedTag = new Tag();
        savedTag.setId(tagId);
        savedTag.setName("important");
        savedTag.setNotes(new ArrayList<>());

        when(tagRepository.save(Mockito.any(Tag.class))).thenReturn(savedTag);

        // Act
        TagResponse response = tagService.create(request);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(tagId);
        Assertions.assertThat(response.getName()).isEqualTo("important");
        Mockito.verify(tagRepository).existsByName("important");
        Mockito.verify(tagRepository).save(Mockito.any(Tag.class));
    }

    @Test
    void TagService_Create_DuplicateName_ThrowsDuplicateTagException() {
        // Arrange
        mockAuthenticatedUser(user);

        TagRequest request = new TagRequest("important");
        when(tagRepository.existsByName("important")).thenReturn(true);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> tagService.create(request))
                .isInstanceOf(DuplicateTagException.class);
        Mockito.verify(tagRepository).existsByName("important");
        Mockito.verify(tagRepository, Mockito.never()).save(Mockito.any(Tag.class));
    }

    @Test
    void TagService_Create_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
        // Arrange
        SecurityContextHolder.clearContext();
        TagRequest request = new TagRequest("important");

        // Act & Assert
        Assertions.assertThatThrownBy(() -> tagService.create(request))
                .isInstanceOf(UserNotAuthenticatedException.class);
        Mockito.verify(tagRepository, Mockito.never()).existsByName(Mockito.any());
    }

    // ==================== getAll ====================

    @Test
    void TagService_GetAll_TagsAreReturnedSuccessfully() {
        // Arrange
        Tag tag1 = new Tag();
        tag1.setId(UUID.randomUUID());
        tag1.setName("work");
        tag1.setNotes(new ArrayList<>());

        Tag tag2 = new Tag();
        tag2.setId(UUID.randomUUID());
        tag2.setName("personal");
        tag2.setNotes(new ArrayList<>());

        when(tagRepository.findAll()).thenReturn(List.of(tag1, tag2));

        // Act
        List<TagResponse> responses = tagService.getAll();

        // Assert
        Assertions.assertThat(responses).hasSize(2);
        Assertions.assertThat(responses.get(0).getName()).isEqualTo("work");
        Assertions.assertThat(responses.get(1).getName()).isEqualTo("personal");
        Mockito.verify(tagRepository).findAll();
    }

    @Test
    void TagService_GetAll_EmptyList_ReturnsEmptyList() {
        // Arrange
        when(tagRepository.findAll()).thenReturn(List.of());

        // Act
        List<TagResponse> responses = tagService.getAll();

        // Assert
        Assertions.assertThat(responses).isEmpty();
        Mockito.verify(tagRepository).findAll();
    }

    // ==================== getById ====================

    @Test
    void TagService_GetById_TagIsReturnedSuccessfully() {
        // Arrange
        UUID tagId = UUID.randomUUID();
        Tag tag = new Tag();
        tag.setId(tagId);
        tag.setName("important");
        tag.setNotes(new ArrayList<>());

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        // Act
        TagResponse response = tagService.getById(tagId);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(tagId);
        Assertions.assertThat(response.getName()).isEqualTo("important");
        Mockito.verify(tagRepository).findById(tagId);
    }

    @Test
    void TagService_GetById_TagNotFound_ThrowsTagNotFoundException() {
        // Arrange
        UUID tagId = UUID.randomUUID();
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> tagService.getById(tagId))
                .isInstanceOf(TagNotFoundException.class);
        Mockito.verify(tagRepository).findById(tagId);
    }

    // ==================== update ====================

    @Test
    void TagService_Update_TagIsUpdatedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        UUID tagId = UUID.randomUUID();
        Tag existingTag = new Tag();
        existingTag.setId(tagId);
        existingTag.setName("old");
        existingTag.setNotes(new ArrayList<>());

        TagRequest request = new TagRequest("new");

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));
        when(tagRepository.existsByName("new")).thenReturn(false);
        when(tagRepository.save(Mockito.any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TagResponse response = tagService.update(tagId, request);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getName()).isEqualTo("new");
        Mockito.verify(tagRepository).findById(tagId);
        Mockito.verify(tagRepository).existsByName("new");
        Mockito.verify(tagRepository).save(existingTag);
    }

    @Test
    void TagService_Update_SameName_TagIsUpdatedSuccessfully() {
        // Arrange
        mockAuthenticatedUser(user);

        UUID tagId = UUID.randomUUID();
        Tag existingTag = new Tag();
        existingTag.setId(tagId);
        existingTag.setName("same");
        existingTag.setNotes(new ArrayList<>());

        TagRequest request = new TagRequest("same");

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));
        when(tagRepository.save(Mockito.any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TagResponse response = tagService.update(tagId, request);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getName()).isEqualTo("same");
        Mockito.verify(tagRepository).findById(tagId);
        Mockito.verify(tagRepository, Mockito.never()).existsByName(Mockito.any());
        Mockito.verify(tagRepository).save(existingTag);
    }

    @Test
    void TagService_Update_TagNotFound_ThrowsTagNotFoundException() {
        // Arrange
        mockAuthenticatedUser(user);
        UUID tagId = UUID.randomUUID();
        TagRequest request = new TagRequest("new");

        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> tagService.update(tagId, request))
                .isInstanceOf(TagNotFoundException.class);
        Mockito.verify(tagRepository).findById(tagId);
        Mockito.verify(tagRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void TagService_Update_DuplicateName_ThrowsDuplicateTagException() {
        // Arrange
        mockAuthenticatedUser(user);

        UUID tagId = UUID.randomUUID();
        Tag existingTag = new Tag();
        existingTag.setId(tagId);
        existingTag.setName("old");
        existingTag.setNotes(new ArrayList<>());

        TagRequest request = new TagRequest("existing");

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));
        when(tagRepository.existsByName("existing")).thenReturn(true);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> tagService.update(tagId, request))
                .isInstanceOf(DuplicateTagException.class);
        Mockito.verify(tagRepository).findById(tagId);
        Mockito.verify(tagRepository).existsByName("existing");
        Mockito.verify(tagRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void TagService_Update_UserNotAuthenticated_ThrowsUserNotAuthenticatedException() {
        // Arrange
        SecurityContextHolder.clearContext();
        UUID tagId = UUID.randomUUID();
        TagRequest request = new TagRequest("new");

        // Act & Assert
        Assertions.assertThatThrownBy(() -> tagService.update(tagId, request))
                .isInstanceOf(UserNotAuthenticatedException.class);
        Mockito.verify(tagRepository, Mockito.never()).findById(Mockito.any());
    }

    // ==================== tagsCount ====================

    @Test
    void TagService_TagsCount_TagsAreCountedSuccessfully() {
        // Arrange
        when(tagRepository.count()).thenReturn(42L);

        // Act
        long count = tagService.tagsCount();

        // Assert
        Assertions.assertThat(count).isEqualTo(42L);
        Mockito.verify(tagRepository).count();
    }

    // ==================== delete ====================

    @Test
    void TagService_Delete_TagIsDeletedSuccessfully() {
        // Arrange
        UUID tagId = UUID.randomUUID();
        Tag tag = new Tag();
        tag.setId(tagId);
        tag.setName("to-delete");
        tag.setNotes(new ArrayList<>());

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        // Act
        tagService.delete(tagId);

        // Assert
        Mockito.verify(tagRepository).findById(tagId);
        Mockito.verify(tagRepository).delete(tag);
    }

    @Test
    void TagService_Delete_TagNotFound_ThrowsTagNotFoundException() {
        // Arrange
        UUID tagId = UUID.randomUUID();

        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> tagService.delete(tagId))
                .isInstanceOf(TagNotFoundException.class);
        Mockito.verify(tagRepository).findById(tagId);
        Mockito.verify(tagRepository, Mockito.never()).delete(Mockito.any());
    }
}
