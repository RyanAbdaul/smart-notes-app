package com.smartnotes.app.backend.rest;

import com.smartnotes.app.backend.request.TagRequest;
import com.smartnotes.app.backend.response.TagResponse;
import com.smartnotes.app.backend.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@Tag(name = "Tags", description = "Tags management API")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "Create a new tag")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public TagResponse create(@Valid @RequestBody TagRequest request) {
        return tagService.create(request);
    }

    @Operation(summary = "Get all tags")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<TagResponse> getAll() {
        return tagService.getAll();
    }

    @Operation(summary = "Get a tag by id")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public TagResponse getById(@PathVariable UUID id) {
        return tagService.getById(id);
    }

    @Operation(summary = "Update an existing tag")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public TagResponse update(@PathVariable UUID id, @Valid @RequestBody TagRequest request) {
        return tagService.update(id, request);
    }

    @Operation(summary = "Count total tags")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/count")
    public Long count() {
        return tagService.tagsCount();
    }

    @Operation(summary = "Delete a tag by id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        tagService.delete(id);
    }
}