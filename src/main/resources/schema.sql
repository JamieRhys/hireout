CREATE TABLE IF NOT EXISTS table_users(
    uuid UUID PRIMARY KEY UNIQUE,
    username VARCHAR NOT NULL,
    password VARCHAR NOT NULL,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);