package com.smartnotes.app.backend.repository;

import com.smartnotes.app.backend.entity.Note;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.entity.Notebook;
import com.smartnotes.app.backend.entity.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.util.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
class NoteRepositoryTests {

    @Autowired
    private NoteRepository noteRepository;

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

    private Note buildNote(String title, String description) {
        Note note = new Note();
        note.setTitle(title);
        note.setDescription(description);
        note.setOwner(owner);
        note.setPinned(false);
        return note;
    }

    @Test
    void NoteRepository_CreateNote_NoteIsSavedSuccessfully() {
        Note note = buildNote("Title", "Description");

        Note savedNote = noteRepository.save(note);

        Assertions.assertNotNull(savedNote.getId());
        Assertions.assertEquals("Title", savedNote.getTitle());
        Assertions.assertEquals(owner.getId(), savedNote.getOwner().getId());
    }



    @Test
    void NoteRepository_FindById_NoteDoesNotExist_ReturnsEmpty() {
        // Act
        Optional<Note> foundNote = noteRepository.findById(UUID.randomUUID());

        // Assert
        Assertions.assertFalse(foundNote.isPresent());
    }

    @Test
    void NoteRepository_FindById_NoteExists_ReturnsNote() {
        Note note = buildNote("Title", "Description");
        Note savedNote = noteRepository.save(note);

        Optional<Note> foundNote = noteRepository.findById(savedNote.getId());

        Assertions.assertTrue(foundNote.isPresent());
        Assertions.assertEquals(savedNote.getId(), foundNote.get().getId());
    }

    @Test
    void NoteRepository_FindAll_NoNotes_ReturnsEmptyList() {
        // Act
        Iterable<Note> notes = noteRepository.findAll();

        // Assert
        int count = 0;
        for (Note n : notes) count++;
        Assertions.assertEquals(0, count);
    }

    @Test
    void NoteRepository_FindAllByOrderByIsPinnedDesc_FirstNoteIsPinned() {
        // Act
        Note pinnedNote = new Note();
        pinnedNote.setPinned(true);
        pinnedNote.setOwner(owner);
        pinnedNote.setTitle("test");
        pinnedNote.setDescription("test");

        Note normalNote = new Note();
        normalNote.setPinned(true);
        normalNote.setOwner(owner);
        normalNote.setTitle("test");
        normalNote.setDescription("test");

        noteRepository.save(pinnedNote);
        noteRepository.save(normalNote);

        List<Note> notes = noteRepository.findAll();

        // Assert
        boolean note = notes.getFirst().isPinned();
        Assertions.assertTrue(note);
    }

    @Test
    void NoteRepository_FindAll_NotesExist_ReturnsNotes() {
        noteRepository.save(buildNote("Title1", "Desc1"));
        noteRepository.save(buildNote("Title2", "Desc2"));

        Iterable<Note> notes = noteRepository.findAll();

        int count = 0;
        for (Note n : notes) count++;
        Assertions.assertEquals(2, count);
    }

    @Test
    void NoteRepository_DeleteById_NoteIsDeletedSuccessfully() {
        Note note = buildNote("Title", "Description");
        Note savedNote = noteRepository.save(note);

        noteRepository.deleteById(savedNote.getId());
        Optional<Note> deletedNote = noteRepository.findById(savedNote.getId());

        Assertions.assertFalse(deletedNote.isPresent());
    }
    
    @Test
    void NoteRepository_FindNotes_ByOwnerId_ReturnsOnlyUserNotes() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setFirstName("Other");
        anotherUser.setLastName("User");
        anotherUser.setEmail("other@example.com");
        anotherUser.setPassword("password");
        userRepository.save(anotherUser);

        Note userNote = buildNote("My Note", "My Description");
        noteRepository.save(userNote);

        Note otherNote = new Note();
        otherNote.setTitle("Other Note");
        otherNote.setDescription("Other Description");
        otherNote.setOwner(anotherUser);
        otherNote.setPinned(false);
        noteRepository.save(otherNote);

        // Act
        List<Note> results = noteRepository.findNotes(owner.getId(), null, null);

        // Assert
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("My Note", results.get(0).getTitle());
    }

    @Test
    void NoteRepository_FindNotes_WithSearchFilter_ReturnsMatchingNotes() {
        // Arrange
        noteRepository.save(buildNote("Grocery list", "Milk, eggs, bread"));
        noteRepository.save(buildNote("Project ideas", "Build a notes app"));

        // Act
        List<Note> results = noteRepository.findNotes(owner.getId(), null, "eggs");

        // Assert
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("Grocery list", results.get(0).getTitle());
    }

    @Test
    void NoteRepository_FindNotes_WithIdFilter_ReturnsSingleNote() {
        // Arrange
        Note note1 = noteRepository.save(buildNote("First Note", "Desc 1"));
        noteRepository.save(buildNote("Second Note", "Desc 2"));

        // Act
        List<Note> results = noteRepository.findNotes(owner.getId(), note1.getId(), null);

        // Assert
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("First Note", results.get(0).getTitle());
    }

    @Test
    void NoteRepository_ExistsById_NoteExists_ReturnsTrue() {
        Note note = buildNote("Title", "Description");
        Note savedNote = noteRepository.save(note);

        boolean exists = noteRepository.existsById(savedNote.getId());

        Assertions.assertTrue(exists);
    }
}
