CREATE TABLE tags (
                      id UUID PRIMARY KEY,
                      name VARCHAR(255) NOT NULL
);

CREATE TABLE note_tags (
                           note_id UUID NOT NULL,
                           tag_id UUID NOT NULL,
                           PRIMARY KEY (note_id, tag_id),
                           CONSTRAINT fk_note_tags_note FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE,
                           CONSTRAINT fk_note_tags_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);