ALTER TABLE books ADD COLUMN title_normalized TEXT GENERATED ALWAYS AS (LOWER(title)) STORED;

CREATE UNIQUE INDEX uq_books_title_norm_author_publisher
    ON books (title_normalized, author_id, publisher_id);