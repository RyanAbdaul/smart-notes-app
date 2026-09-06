-- V1__create_notes_table.sql
-- Initial schema: Create notes table

CREATE TABLE notes (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create index on created_at for sorting
CREATE INDEX idx_notes_created_at ON notes(created_at DESC);
