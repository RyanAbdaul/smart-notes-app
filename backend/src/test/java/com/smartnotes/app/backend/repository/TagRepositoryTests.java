package com.smartnotes.app.backend.repository;

import com.smartnotes.app.backend.entity.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
class TagRepositoryTests {

    @Autowired
    private TagRepository tagRepository;

    @Test
    void TagRepository_CreateTag_TagIsSavedSuccessfully() {
        // Arrange
        Tag tag = new Tag();
        tag.setName("work");

        // Act
        Tag savedTag = tagRepository.save(tag);

        // Assert
        Assertions.assertNotNull(savedTag);
        Assertions.assertEquals("work", savedTag.getName());
        Assertions.assertNotNull(savedTag.getId());
    }

    @Test
    void TagRepository_FindById_TagExists_ReturnsTag() {
        // Arrange
        Tag tag = new Tag();
        tag.setName("personal");
        Tag savedTag = tagRepository.save(tag);

        // Act
        Optional<Tag> result = tagRepository.findById(savedTag.getId());

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(savedTag.getId(), result.get().getId());
        Assertions.assertEquals("personal", result.get().getName());
    }

    @Test
    void TagRepository_FindById_TagNotFound_ReturnsEmpty() {
        // Act
        Optional<Tag> result = tagRepository.findById(UUID.randomUUID());

        // Assert
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    void TagRepository_FindAll_TagsAreFetchedSuccessfully() {
        // Arrange
        Tag tag1 = new Tag();
        tag1.setName("work");
        Tag tag2 = new Tag();
        tag2.setName("urgent");
        tagRepository.save(tag1);
        tagRepository.save(tag2);

        // Act
        List<Tag> tags = tagRepository.findAll();

        // Assert
        Assertions.assertFalse(tags.isEmpty());
        Assertions.assertEquals(2, tags.size());
    }

    @Test
    void TagRepository_FindAll_NoTags_ReturnsEmptyList() {
        // Act
        List<Tag> tags = tagRepository.findAll();

        // Assert
        Assertions.assertTrue(tags.isEmpty());
    }

    @Test
    void TagRepository_FindByName_TagExists_ReturnsTag() {
        // Arrange
        Tag tag = new Tag();
        tag.setName("work");
        tagRepository.save(tag);

        // Act
        Optional<Tag> result = tagRepository.findByName("work");

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("work", result.get().getName());
    }

    @Test
    void TagRepository_FindByName_TagNotFound_ReturnsEmpty() {
        // Act
        Optional<Tag> result = tagRepository.findByName("nonexistent");

        // Assert
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    void TagRepository_ExistsByName_TagExists_ReturnsTrue() {
        // Arrange
        Tag tag = new Tag();
        tag.setName("work");
        tagRepository.save(tag);

        // Act
        boolean exists = tagRepository.existsByName("work");

        // Assert
        Assertions.assertTrue(exists);
    }

    @Test
    void TagRepository_ExistsByName_TagNotFound_ReturnsFalse() {
        // Act
        boolean exists = tagRepository.existsByName("nonexistent");

        // Assert
        Assertions.assertFalse(exists);
    }

    @Test
    void TagRepository_DeleteById_TagIsDeletedSuccessfully() {
        // Arrange
        Tag tag = new Tag();
        tag.setName("temp");
        Tag savedTag = tagRepository.save(tag);

        // Act
        tagRepository.deleteById(savedTag.getId());
        Optional<Tag> deleted = tagRepository.findById(savedTag.getId());

        // Assert
        Assertions.assertFalse(deleted.isPresent());
    }

    @Test
    void TagRepository_ExistsById_TagExists_ReturnsTrue() {
        // Arrange
        Tag tag = new Tag();
        tag.setName("work");
        Tag savedTag = tagRepository.save(tag);

        // Act
        boolean exists = tagRepository.existsById(savedTag.getId());

        // Assert
        Assertions.assertTrue(exists);
    }

    @Test
    void TagRepository_ExistsById_TagNotFound_ReturnsFalse() {
        // Act
        boolean exists = tagRepository.existsById(UUID.randomUUID());

        // Assert
        Assertions.assertFalse(exists);
    }
}
