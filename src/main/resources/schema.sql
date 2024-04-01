CREATE TABLE IF NOT EXISTS table_users(
    uuid UUID PRIMARY KEY UNIQUE,
    username VARCHAR UNIQUE,
    password VARCHAR NOT NULL,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS table_user_roles(
    id INT PRIMARY KEY UNIQUE,
    role_name VARCHAR UNIQUE
);