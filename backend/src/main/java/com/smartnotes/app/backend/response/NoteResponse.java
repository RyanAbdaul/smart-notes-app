package com.smartnotes.app.backend.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartnotes.app.backend.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NoteResponse {

    private UUID id;
    private String title;
    private String description;
    // private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String imageUrl;
    private List<TagResponse> tags;

    private boolean pinned;
}
