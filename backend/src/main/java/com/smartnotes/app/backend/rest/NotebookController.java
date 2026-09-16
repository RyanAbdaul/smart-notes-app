package com.smartnotes.app.backend.rest;

import com.smartnotes.app.backend.service.NotebookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notebooks")
@RequiredArgsConstructor
@Tag(name = "Notebooks", description = "Notebook management API")
public class NotebookController {

    private final NotebookService notebookService;
}
