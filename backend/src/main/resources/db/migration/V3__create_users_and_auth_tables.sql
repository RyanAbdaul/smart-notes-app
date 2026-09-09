-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create user_authority table for storing user roles
CREATE TABLE user_authority (
    user_id BIGINT NOT NULL,
    authority VARCHAR(255) NOT NULL,
    CONSTRAINT fk_user_authority_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Add user_id column to notes table
ALTER TABLE notes ADD COLUMN user_id BIGINT NOT NULL DEFAULT 1;

-- Add foreign key constraint
ALTER TABLE notes ADD CONSTRAINT fk_notes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Create index on user_id for better query performance
CREATE INDEX idx_notes_user_id ON notes(user_id);
CREATE INDEX idx_user_authority_user_id ON user_authority(user_id);
CREATE INDEX idx_users_email ON users(email);
