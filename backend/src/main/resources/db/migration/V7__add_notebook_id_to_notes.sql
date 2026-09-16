ALTER TABLE notes ADD COLUMN notebook_id UUID;
ALTER TABLE notes ADD CONSTRAINT fk_notes_notebook FOREIGN KEY (notebook_id) REFERENCES notebooks (id) ON DELETE SET NULL;
CREATE INDEX idx_notes_notebook_id ON notes (notebook_id);
