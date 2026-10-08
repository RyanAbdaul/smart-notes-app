ALTER TABLE tags DROP CONSTRAINT IF EXISTS tags_name_key;
DROP INDEX IF EXISTS tags_name_key;

ALTER TABLE tags
    ADD CONSTRAINT uq_tags_owner_name UNIQUE (owner_id, name);