INSERT INTO table_users(uuid, first_name, last_name, username, password)
SELECT
    gen_random_uuid(),
    'Admin',
    'User',
    'admin.user',
    'password123'
WHERE NOT EXISTS (
      SELECT 1 FROM table_users WHERE username = 'admin.user'
);