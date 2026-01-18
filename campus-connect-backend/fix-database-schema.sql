-- Fix database schema: Remove duplicate column issue
-- This script will consolidate the collegename and college_name columns

-- Step 1: Check if both columns exist
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'users' 
AND column_name IN ('collegename', 'college_name');

-- Step 2: If collegename column exists, drop it (we'll use college_name as primary)
ALTER TABLE users DROP COLUMN IF EXISTS collegename;

-- Step 3: Verify the fix
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'users' 
AND column_name LIKE '%college%';
