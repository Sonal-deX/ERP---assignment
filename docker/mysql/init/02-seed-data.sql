-- Optional: Create any initial data or additional configurations
USE service_center_db;

-- You can add any initial data here that you want to be present when the container starts
-- For example:

-- Create a default admin user (this would typically be handled by your Spring Boot application)
-- INSERT INTO users (email, password, role, active) VALUES 
-- ('admin@servicecenter.com', '$2a$10$...', 'ADMIN', true);

-- Create default system settings
-- INSERT INTO system_settings (setting_key, setting_value) VALUES
-- ('system_name', 'Service Center Management'),
-- ('version', '1.0.0'),
-- ('maintenance_mode', 'false');

-- Add any other initial data your application needs

SELECT 'Initial data setup completed' AS status;