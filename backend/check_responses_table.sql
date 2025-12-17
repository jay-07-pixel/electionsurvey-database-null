-- Check the actual structure of the responses table
DESCRIBE responses;

-- Or use this:
SHOW COLUMNS FROM responses;

-- Check what columns actually exist
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'responses' 
AND TABLE_SCHEMA = 'election_survey';

