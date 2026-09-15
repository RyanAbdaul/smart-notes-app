package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.entity.Tag;
import com.smartnotes.app.backend.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Tag create(Tag tag) {
        return tagRepository.save(tag);
    }

    public List<Tag> getAll() {
        return (List<Tag>) tagRepository.findAll();
    }

    public Tag getById(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found: " + id));
    }

    public Tag update(UUID id, Tag updated) {
        Tag existing = getById(id);
        existing.setName(updated.getName());
        return tagRepository.save(existing);
    }

    public long tagsCount() {
        return tagRepository.count();
    }

    public void delete(UUID id) {
        tagRepository.deleteById(id);
    }
}