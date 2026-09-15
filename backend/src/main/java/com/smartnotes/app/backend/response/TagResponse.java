package com.smartnotes.app.backend.response;

import com.smartnotes.app.backend.entity.Note;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagResponse {

    private UUID id;
    private String name;
    private List<Note> notes;
}
