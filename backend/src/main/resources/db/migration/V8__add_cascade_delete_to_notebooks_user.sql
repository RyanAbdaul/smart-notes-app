ALTER TABLE notebooks DROP CONSTRAINT fk_notebooks_user;
ALTER TABLE notebooks ADD CONSTRAINT fk_notebooks_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
