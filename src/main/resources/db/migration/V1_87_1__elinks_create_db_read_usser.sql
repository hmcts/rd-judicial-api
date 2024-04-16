-- Grant usage and select privs on Reader user

DO
$do$
BEGIN
IF EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = '${dbReaderUserName}') THEN

RAISE NOTICE 'Role "${dbReaderUserName}" already exists. Skipping.';
ELSE
BEGIN
CREATE ROLE "${dbReaderUserName}" WITH LOGIN IN ROLE azure_ad_user;
EXCEPTION
WHEN duplicate_object THEN
RAISE NOTICE 'Role "${dbReaderUserName}" was just created by a concurrent transaction. Skipping.';
END;
END IF;
END
$do$;
