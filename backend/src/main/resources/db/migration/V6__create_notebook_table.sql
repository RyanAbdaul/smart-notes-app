CREATE TABLE notebooks (
                           id UUID NOT NULL,
                           name VARCHAR(255) NOT NULL,
                           user_id UUID NOT NULL,
                           created_at TIMESTAMP NOT NULL,
                           updated_at TIMESTAMP NOT NULL,
                           CONSTRAINT pk_notebooks PRIMARY KEY (id),
                           CONSTRAINT fk_notebooks_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_notebooks_user_id ON notebooks (user_id);