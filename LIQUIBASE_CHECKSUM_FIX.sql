-- =============================================================================
-- LIQUIBASE CHECKSUM FIX - Fix Module Startup Issues
-- =============================================================================
--
-- This script fixes the "Validation Failed: 1 change sets check sum" error
-- that occurs when you modify a changeset that has already been executed.
--
-- Run this script BEFORE starting the CDS module again.
-- =============================================================================

-- Step 1: Clear the old changeset records from liquibase changelog
-- This allows the modified changesets to be re-executed with new checksums

DELETE FROM liquibasechangelog
WHERE id LIKE 'cds-%'
  AND filename = 'liquibase.xml';

-- Step 2: Verify the deletion
SELECT 'Changesets cleared. Verify below:' as Status;
SELECT id, filename, author, dateexecuted, exectype FROM liquibasechangelog
WHERE id LIKE 'cds-%' OR filename = 'liquibase.xml';

-- Step 3: Also clear the liquibasechangeloglock if it exists to prevent lock issues
-- Uncomment below if you get lock-related errors
-- TRUNCATE TABLE liquibasechangeloglock;

-- Result: The CDS module changesets will now be re-executed with correct checksums
-- when you restart the OpenMRS application.

COMMIT;

