-- Check the structure of the users table
DESCRIBE users;

-- Or use this to see all columns:
SHOW COLUMNS FROM users;

-- If phone column doesn't exist, you can add it:
-- ALTER TABLE users ADD COLUMN phone VARCHAR(15) UNIQUE AFTER name;

-- Or if the column has a different name (like phone_number), check:
-- SELECT * FROM users LIMIT 1;

