delete from tags;
delete from notes;
ALTER TABLE tags ADD COLUMN owner_id UUID NOT NULL;