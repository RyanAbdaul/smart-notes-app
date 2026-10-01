package com.smartnotes.app.backend.controller;

import com.smartnotes.app.backend.annotation.RateLimit;
import com.smartnotes.app.backend.request.TagRequest;
import com.smartnotes.app.backend.response.TagResponse;
import com.smartnotes.app.backend.service.RateLimiterService;
import com.smartnotes.app.backend.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tags")
@AllArgsConstructor
@Tag(name = "Tags", description = "Tags management API")
public class TagController {

    private final TagService tagService;
    private final RateLimiterService rateLimiterService;

    @Operation(summary = "Create a new tag")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @RateLimit(endpoint = "create_tag")
    public TagResponse create(@Valid @RequestBody TagRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        if (!rateLimiterService.isAllowed("create_tag", ip)) {
            throw new RuntimeException("Too many tag creation attempts, try again later");
        }
        return tagService.create(request);
    }

    @Operation(summary = "Get all tags")
    @ResponseStatus(HttpStatus.OK)
    @RateLimit(endpoint = "create_all_tags")
    @GetMapping
    public List<TagResponse> getAll() {
        return tagService.getAll();
    }

    @Operation(summary = "Get a tag by id")
    @ResponseStatus(HttpStatus.OK)
    @RateLimit(endpoint = "get_tag_by_id")
    @GetMapping("/{id}")
    public TagResponse getById(@PathVariable UUID id) {
        return tagService.getById(id);
    }

    @Operation(summary = "Update an existing tag")
    @ResponseStatus(HttpStatus.OK)
    @RateLimit(endpoint = "update_tag_by_id")
    @PutMapping("/{id}")
    public TagResponse update(@PathVariable UUID id, @Valid @RequestBody TagRequest request) {
        return tagService.update(id, request);
    }

    @Operation(summary = "Count total tags")
    @ResponseStatus(HttpStatus.OK)
    @RateLimit(endpoint = "count_tags")
    @GetMapping("/count")
    public Long count() {
        return tagService.tagsCount();
    }

    @Operation(summary = "Delete a tag by id")
    @RateLimit(endpoint = "delete_tag")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        tagService.delete(id);
    }
}