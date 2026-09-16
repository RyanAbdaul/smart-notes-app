package com.smartnotes.app.backend.repository;

import com.smartnotes.app.backend.entity.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, UUID> {

    @Query("SELECT n FROM Notebook n LEFT JOIN FETCH n.notes WHERE n.owner.id = ?1")
    List<Notebook> findAllWithNotesByOwnerId(UUID userId);

    @Query("SELECT COUNT(n) FROM Notebook n WHERE n.owner.id = ?1")
    Long countByOwnerId(UUID userId);
}