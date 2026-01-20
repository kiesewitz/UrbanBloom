-- Create schema for user module
CREATE SCHEMA IF NOT EXISTS module_user_schema;

-- Migrate existing user_profiles table to module_user_schema
ALTER TABLE user_profiles SET SCHEMA module_user_schema;

-- Add missing NOT NULL constraints and update column names if needed
ALTER TABLE module_user_schema.user_profiles
    ALTER COLUMN first_name SET NOT NULL,
    ALTER COLUMN last_name SET NOT NULL,
    ALTER COLUMN role SET NOT NULL;

-- Rename external_id to external_user_id for clarity
ALTER TABLE module_user_schema.user_profiles
    RENAME COLUMN external_id TO external_user_id;

-- Update indexes to reflect new column name
DROP INDEX IF EXISTS idx_user_profiles_external_id;
CREATE INDEX idx_user_profiles_external_user_id ON module_user_schema.user_profiles(external_user_id);
