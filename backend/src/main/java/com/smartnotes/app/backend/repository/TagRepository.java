package com.smartnotes.app.backend.repository;

import com.smartnotes.app.backend.entity.Note;
import com.smartnotes.app.backend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
    Optional<Tag> findByName(String name);
    boolean existsByName(String name);
    List<Tag> findAllByOwnerId(UUID ownerId);
    boolean existsByNameAndOwnerId(String name, UUID ownerId);
    long countByOwnerId(UUID ownerId);
}
