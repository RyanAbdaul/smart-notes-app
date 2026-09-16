CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Drop dependent FKs first
ALTER TABLE user_authority DROP CONSTRAINT fk_user_authority_user;
ALTER TABLE notes DROP CONSTRAINT fk_notes_user;

-- New UUID id for users
ALTER TABLE users ADD COLUMN new_id UUID DEFAULT gen_random_uuid() NOT NULL;

-- New UUID FK columns, populated from mapping
ALTER TABLE user_authority ADD COLUMN new_user_id UUID;
UPDATE user_authority ua SET new_user_id = u.new_id FROM users u WHERE ua.user_id = u.id;

ALTER TABLE notes ADD COLUMN new_user_id UUID;
UPDATE notes n SET new_user_id = u.new_id FROM users u WHERE n.user_id = u.id;

-- Swap columns
ALTER TABLE users DROP CONSTRAINT users_pkey;
ALTER TABLE users DROP COLUMN id;
ALTER TABLE users RENAME COLUMN new_id TO id;
ALTER TABLE users ADD CONSTRAINT pk_users PRIMARY KEY (id);

ALTER TABLE user_authority DROP COLUMN user_id;
ALTER TABLE user_authority RENAME COLUMN new_user_id TO user_id;
ALTER TABLE user_authority ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE notes DROP COLUMN user_id;
ALTER TABLE notes RENAME COLUMN new_user_id TO user_id;
ALTER TABLE notes ALTER COLUMN user_id SET NOT NULL;

-- Recreate FKs and indexes
ALTER TABLE user_authority ADD CONSTRAINT fk_user_authority_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE notes ADD CONSTRAINT fk_notes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

CREATE INDEX idx_notes_user_id ON notes(user_id);
CREATE INDEX idx_user_authority_user_id ON user_authority(user_id);