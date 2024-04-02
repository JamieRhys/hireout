CREATE TABLE IF NOT EXISTS table_users(
    uuid UUID PRIMARY KEY UNIQUE,
    username VARCHAR UNIQUE,
    password VARCHAR NOT NULL,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS table_user_roles(
    id SERIAL PRIMARY KEY UNIQUE,
    role_name VARCHAR UNIQUE
);

CREATE TABLE IF NOT EXISTS table_user_and_role(
    user_id UUID,
    role_id INT,
    PRIMARY KEY(user_id, role_id),
    FOREIGN KEY(user_id) REFERENCES table_users(uuid),
    FOREIGN KEY(role_id) REFERENCES table_user_roles(id)
);