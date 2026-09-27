package com.smartnotes.app.backend.repository;

import com.smartnotes.app.backend.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    @Query("SELECT n FROM Note n WHERE n.owner.id = :ownerId " +
            "AND (:id IS NULL OR n.id = :id) " +
            "AND (CAST(:search AS string) IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
            "OR LOWER(n.description) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))")
    List<Note> findNotes(@Param("ownerId") UUID ownerId,
                          @Param("id") UUID id,
                          @Param("search") String search);
}
