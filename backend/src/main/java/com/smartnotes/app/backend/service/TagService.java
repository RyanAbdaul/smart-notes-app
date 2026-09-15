package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.entity.Tag;
import com.smartnotes.app.backend.repository.TagRepository;
import com.smartnotes.app.backend.request.TagRequest;
import com.smartnotes.app.backend.response.TagResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public TagResponse create(TagRequest request) {
        Tag tag = new Tag();
        tag.setName(request.getName());
        return toResponse(tagRepository.save(tag));
    }
    public List<TagResponse> getAll() {
        return tagRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }
    public TagResponse getById(UUID id) {
        return toResponse(findEntity(id));
    }

    public TagResponse update(UUID id, TagRequest request) {
        Tag existing = findEntity(id);
        existing.setName(request.getName());
        return toResponse(tagRepository.save(existing));
    }

    public long tagsCount() {
        return tagRepository.count();
    }

    public void delete(UUID id) {
        tagRepository.deleteById(id);
    }

    private Tag findEntity(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found: " + id));
    }

    private TagResponse toResponse(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName(), tag.getNotes());
    }
}