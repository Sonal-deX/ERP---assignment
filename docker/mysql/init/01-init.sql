-- Service Center Management Database Initialization
-- This script runs when the MySQL container starts for the first time

-- Set timezone to UTC
SET time_zone = '+00:00';

-- Enable foreign key checks
SET foreign_key_checks = 1;

-- Use the created database
USE service_center_db;

-- Set proper character set and collation for the database
ALTER DATABASE service_center_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Grant all privileges to the service user on the database
GRANT ALL PRIVILEGES ON service_center_db.* TO 'serviceuser'@'%';
FLUSH PRIVILEGES;

-- Create indexes for better performance (will be created by Hibernate but we can optimize)
-- These will be created automatically by Spring Boot JPA, but we can add custom ones here if needed

-- Log successful initialization
SELECT 'Service Center Database initialization completed successfully' AS status;
SELECT CONCAT('Database: ', DATABASE()) AS current_database;
SELECT CONCAT('Character Set: ', @@character_set_database) AS character_set;
SELECT CONCAT('Collation: ', @@collation_database) AS collation;
SELECT NOW() AS initialization_time;