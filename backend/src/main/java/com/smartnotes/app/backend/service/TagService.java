package com.smartnotes.app.backend.service;

import com.smartnotes.app.backend.annotation.RequireOwnership;
import com.smartnotes.app.backend.entity.Tag;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.exception.DuplicateTagException;
import com.smartnotes.app.backend.exception.TagNotFoundException;
import com.smartnotes.app.backend.exception.UserNotAuthenticatedException;
import com.smartnotes.app.backend.repository.TagRepository;
import com.smartnotes.app.backend.request.TagRequest;
import com.smartnotes.app.backend.response.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@RequireOwnership(entity = Tag.class)
public class TagService {
    private final TagRepository tagRepository;

    @Caching(evict = {
            @CacheEvict(value = "allTags", key = "#root.target.getCurrentUser().id.toString()"),
            @CacheEvict(value = "tagCount", key = "#root.target.getCurrentUser().id.toString()")
    })
    public TagResponse create(TagRequest request) {
        User currentUser = getCurrentUser();
        if (tagRepository.existsByNameAndOwnerId(request.getName(), currentUser.getId())) {
            throw new DuplicateTagException(request.getName(), currentUser.getId());
        }
        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setOwner(currentUser);
        return toResponse(tagRepository.save(tag));
    }

    @Cacheable(value = "allTags", key = "#root.target.getCurrentUser().id.toString()")
    public List<TagResponse> getAll() {
        return tagRepository.findAllByOwnerId(getCurrentUser().getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "tagById", key = "#root.target.getCurrentUser().id + ':' + #id")
    public TagResponse getById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Caching(
            put = @CachePut(value = "tagById", key = "#root.target.getCurrentUser().id + ':' + #id"),
            evict = @CacheEvict(value = "allTags", key = "#root.target.getCurrentUser().id.toString()")
    )
    public TagResponse update(UUID id, TagRequest request) {
        User currentUser = getCurrentUser();
        Tag existing = findEntity(id);
        if (!existing.getName().equals(request.getName())
                && tagRepository.existsByNameAndOwnerId(request.getName(), currentUser.getId())) {
            throw new DuplicateTagException(request.getName(), currentUser.getId());
        }
        existing.setName(request.getName());
        return toResponse(tagRepository.save(existing));
    }

    @Cacheable(value = "tagCount", key = "#root.target.getCurrentUser().id.toString()")
    public long tagsCount() {
        return tagRepository.countByOwnerId(getCurrentUser().getId());
    }

    @Caching(evict = {
            @CacheEvict(value = "tagById", key = "#root.target.getCurrentUser().id + ':' + #id"),
            @CacheEvict(value = "allTags", key = "#root.target.getCurrentUser().id.toString()"),
            @CacheEvict(value = "tagCount", key = "#root.target.getCurrentUser().id.toString()")
    })
    public void delete(UUID id) {
        tagRepository.delete(findEntity(id));
    }

    private Tag findEntity(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
    }

    private TagResponse toResponse(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }
        return (User) authentication.getPrincipal();
    }
}
