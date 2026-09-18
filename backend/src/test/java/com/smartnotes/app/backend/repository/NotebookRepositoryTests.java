package com.smartnotes.app.backend.repository;

import com.smartnotes.app.backend.entity.Notebook;
import com.smartnotes.app.backend.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
class NotebookRepositoryTests {

    @Autowired
    private NotebookRepository notebookRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setEmail("john.doe@example.com");
        owner.setPassword("encodedPassword");
        userRepository.save(owner);
    }

    private Notebook buildNotebook(String name) {
        Notebook notebook = new Notebook();
        notebook.setName(name);
        notebook.setOwner(owner);
        return notebook;
    }

    @Test
    void NotebookRepository_CreateNotebook_NotebookIsSavedSuccessfully() {
        Notebook notebook = buildNotebook("Study");

        Notebook savedNotebook = notebookRepository.save(notebook);

        Assertions.assertNotNull(savedNotebook.getId());
        Assertions.assertEquals("Study", savedNotebook.getName());
        Assertions.assertEquals(owner.getId(), savedNotebook.getOwner().getId());
    }

    @Test
    void NotebookRepository_FindById_NotebookExists_ReturnsNotebook() {
        Notebook notebook = buildNotebook("Study");
        Notebook savedNotebook = notebookRepository.save(notebook);

        Optional<Notebook> foundNotebook = notebookRepository.findById(savedNotebook.getId());

        Assertions.assertTrue(foundNotebook.isPresent());
        Assertions.assertEquals(savedNotebook.getId(), foundNotebook.get().getId());
    }

    @Test
    void NotebookRepository_FindById_NotebookDoesNotExist_ReturnsEmpty() {
        Optional<Notebook> foundNotebook = notebookRepository.findById(UUID.randomUUID());

        Assertions.assertFalse(foundNotebook.isPresent());
    }

    @Test
    void NotebookRepository_FindAllWithNotesByOwnerId_ReturnsNotebooksWithNotes() {
        Notebook notebook = buildNotebook("Study");
        notebookRepository.save(notebook);

        List<Notebook> foundNotebooks = notebookRepository.findAllWithNotesByOwnerId(owner.getId());

        Assertions.assertFalse(foundNotebooks.isEmpty());
        Assertions.assertEquals(1, foundNotebooks.size());
        Assertions.assertEquals("Study", foundNotebooks.get(0).getName());
    }

    @Test
    void NotebookRepository_CountByOwnerId_ReturnsCount() {
        notebookRepository.save(buildNotebook("Study"));
        notebookRepository.save(buildNotebook("Work"));

        Long count = notebookRepository.countByOwnerId(owner.getId());

        Assertions.assertEquals(2L, count);
    }

    @Test
    void NotebookRepository_ExistsByNameAndOwnerId_ReturnsTrue() {
        notebookRepository.save(buildNotebook("Study"));

        boolean exists = notebookRepository.existsByNameAndOwnerId("Study", owner.getId());

        Assertions.assertTrue(exists);
    }

    @Test
    void NotebookRepository_FindByNameAndOwnerId_ReturnsNotebook() {
        notebookRepository.save(buildNotebook("Study"));

        Optional<Notebook> foundNotebook = notebookRepository.findByNameAndOwnerId("Study", owner.getId());

        Assertions.assertTrue(foundNotebook.isPresent());
        Assertions.assertEquals("Study", foundNotebook.get().getName());
    }

    @Test
    void NotebookRepository_UpdateNotebook_NameIsUpdatedSuccessfully() {
        Notebook notebook = buildNotebook("OldName");
        Notebook savedNotebook = notebookRepository.save(notebook);

        savedNotebook.setName("NewName");
        Notebook updatedNotebook = notebookRepository.save(savedNotebook);

        Assertions.assertEquals("NewName", updatedNotebook.getName());
        Assertions.assertEquals(savedNotebook.getId(), updatedNotebook.getId());
    }

    @Test
    void NotebookRepository_FindAll_NotebooksAreFetchedSuccessfully() {
        notebookRepository.save(buildNotebook("Study"));
        notebookRepository.save(buildNotebook("Work"));

        List<Notebook> notebooks = notebookRepository.findAll();

        Assertions.assertFalse(notebooks.isEmpty());
        Assertions.assertEquals(2, notebooks.size());
    }

    @Test
    void NotebookRepository_FindAll_NoNotebooks_ReturnsEmptyList() {
        List<Notebook> notebooks = notebookRepository.findAll();

        Assertions.assertTrue(notebooks.isEmpty());
    }

    @Test
    void NotebookRepository_DeleteById_NotebookIsDeletedSuccessfully() {
        Notebook notebook = buildNotebook("Study");
        Notebook savedNotebook = notebookRepository.save(notebook);

        notebookRepository.deleteById(savedNotebook.getId());
        Optional<Notebook> deletedNotebook = notebookRepository.findById(savedNotebook.getId());

        Assertions.assertFalse(deletedNotebook.isPresent());
    }
}
