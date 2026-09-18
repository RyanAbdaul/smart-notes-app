package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.entity.Tag;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.exception.DuplicateTagException;
import com.smartnotes.app.backend.exception.TagNotFoundException;
import com.smartnotes.app.backend.exception.UserNotAuthenticatedException;
import com.smartnotes.app.backend.repository.TagRepository;
import com.smartnotes.app.backend.request.TagRequest;
import com.smartnotes.app.backend.response.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public TagResponse create(TagRequest request) {
        User currentUser = getCurrentUser();
        if (tagRepository.existsByName(request.getName())) {
            throw new DuplicateTagException(request.getName(), currentUser.getId());
        }
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
        User currentUser = getCurrentUser();
        Tag existing = findEntity(id);
        if (!existing.getName().equals(request.getName()) && tagRepository.existsByName(request.getName())) {
            throw new DuplicateTagException(request.getName(), currentUser.getId());
        }
        existing.setName(request.getName());
        return toResponse(tagRepository.save(existing));
    }

    public long tagsCount() {
        return tagRepository.count();
    }

    public void delete(UUID id) {
        Tag existing = findEntity(id);
        tagRepository.delete(existing);
    }

    private Tag findEntity(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
    }

    private TagResponse toResponse(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName(), tag.getNotes());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }
        return (User) authentication.getPrincipal();
    }
}