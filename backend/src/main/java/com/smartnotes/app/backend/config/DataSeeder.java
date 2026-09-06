package com.smartnotes.app.backend.config;

import com.smartnotes.app.backend.entity.Note;
import com.smartnotes.app.backend.repository.NoteRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final NoteRepository noteRepository;

    @PostConstruct
    public void seedData() {
        // Only seed if database is empty
        if (noteRepository.count() > 0) {
            log.info("Database already contains data. Skipping seed.");
            return;
        }

        log.info("Seeding database with sample notes...");

        createNote(
                "Welcome to Smart Notes",
                "This is your first note! Smart Notes helps you organize your thoughts and ideas efficiently. You can create, edit, and delete notes as needed.",
                LocalDateTime.of(2026, 9, 1, 10, 0, 0)
        );

        createNote(
                "Meeting Notes - Q4 Planning",
                "Discussed quarterly objectives and key results. Action items: 1) Review budget allocation, 2) Schedule team sync meetings, 3) Define project milestones.",
                LocalDateTime.of(2026, 9, 2, 14, 30, 0)
        );

        createNote(
                "Recipe: Homemade Pizza",
                "Ingredients: Pizza dough, tomato sauce, mozzarella cheese, basil, olive oil. Instructions: 1) Preheat oven to 475°F, 2) Roll out dough, 3) Add toppings, 4) Bake for 12-15 minutes.",
                LocalDateTime.of(2026, 9, 3, 18, 45, 0)
        );

        createNote(
                "Book Recommendations",
                "Must-read books: \"Atomic Habits\" by James Clear, \"Deep Work\" by Cal Newport, \"The Pragmatic Programmer\" by Hunt & Thomas. Great for personal and professional development.",
                LocalDateTime.of(2026, 9, 4, 11, 20, 0)
        );

        createNote(
                "Project Ideas",
                "Ideas for side projects: 1) Personal finance tracker, 2) Habit tracking app, 3) Recipe organizer with meal planning, 4) Workout log with progress charts.",
                LocalDateTime.of(2026, 9, 5, 9, 0, 0)
        );

        createNote(
                "Travel Plans - Summer 2027",
                "Destinations to consider: Barcelona (architecture and beaches), Tokyo (culture and food), Iceland (nature and northern lights). Budget: $3000-4000 per trip.",
                LocalDateTime.of(2026, 9, 5, 20, 15, 0)
        );

        createNote(
                "Learning Goals",
                "Skills to learn this year: Docker & Kubernetes, Advanced SQL optimization, System design patterns, GraphQL. Dedicate 5 hours per week to learning.",
                LocalDateTime.of(2026, 9, 6, 7, 30, 0)
        );

        createNote(
                "Quick Note",
                "Remember to backup all project files before the weekend. Cloud storage sync is scheduled for Friday evening.",
                LocalDateTime.of(2026, 9, 6, 8, 15, 0)
        );

        log.info("Successfully seeded {} notes", noteRepository.count());
    }

    private void createNote(String title, String description, LocalDateTime createdAt) {
        Note note = new Note();
        note.setTitle(title);
        note.setDescription(description);
        // Note: createdAt and updatedAt will be set automatically by @CreationTimestamp and @UpdateTimestamp
        // The LocalDateTime parameter is just for documentation of intended dates
        noteRepository.save(note);
    }
}
