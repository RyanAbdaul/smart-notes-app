package com.smartnotes.app.backend.rest;

import com.smartnotes.app.backend.request.NotebookRequest;
import com.smartnotes.app.backend.response.NotebookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notebooks")
@RequiredArgsConstructor
@Tag(name = "Notebooks", description = "Notebook management API")
public class NotebookController {

    private final NotebookService notebookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new notebook",
            description = "Creates a new notebook with name. The notebook will be associated with the authenticated user."
    )
    public NotebookResponse createNotebook(@Valid @RequestBody NotebookRequest request) {
        return notebookService.createNotebook(request);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get notebook by ID",
            description = "Retrieves a single notebook by its unique identifier. Only returns the notebook if it belongs to the authenticated user."
    )
    public NotebookResponse getNotebookById(@PathVariable UUID id) {
        return notebookService.getNotebookById(id);
    }

    @GetMapping
    @Operation(
            summary = "Get all notebooks",
            description = "Retrieves a list of all notebooks belonging to the authenticated user"
    )
    public List<NotebookResponse> getAllNotebooks() {
        return notebookService.getAllNotebooks();
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update a notebook",
            description = "Updates an existing notebook's name. Only the notebook owner can update it."
    )
    public NotebookResponse updateNotebook(
            @PathVariable UUID id,
            @Valid @RequestBody NotebookRequest request) {
        return notebookService.updateNotebook(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a notebook",
            description = "Permanently deletes a notebook from the system. Only the notebook owner can delete it."
    )
    public void deleteNotebook(@PathVariable UUID id) {
        notebookService.deleteNotebook(id);
    }

    @GetMapping("/count")
    @Operation(
            summary = "Count total notebooks",
            description = "Returns the total number of notebooks belonging to the authenticated user"
    )
    public long countNotebooks() {
        return notebookService.countNotebooks();
    }
}
