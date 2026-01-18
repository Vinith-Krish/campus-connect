package com.campusconnect;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseMigrationRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("Checking database schema...");
            
            // Check if college_name column exists
            String checkColumnQuery = "SELECT column_name FROM information_schema.columns " +
                    "WHERE table_name = 'users' AND column_name = 'college_name'";
            
            var result = jdbcTemplate.queryForList(checkColumnQuery);
            
            if (result.isEmpty()) {
                log.warn("Column 'college_name' does not exist. Running migration...");
                
                // Add college_name column
                jdbcTemplate.execute("ALTER TABLE users ADD COLUMN college_name VARCHAR(255)");
                log.info("Column 'college_name' added successfully");
                
                // Update existing users with default value
                jdbcTemplate.execute("UPDATE users SET college_name = 'Default College' WHERE college_name IS NULL");
                log.info("Updated existing users with default college name");
                
                // Make column NOT NULL
                jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN college_name SET NOT NULL");
                log.info("Column 'college_name' set to NOT NULL");
                
                log.info("Migration completed successfully!");
            } else {
                log.info("Column 'college_name' already exists. No migration needed.");
            }
        } catch (Exception e) {
            log.error("Error during migration: {}", e.getMessage(), e);
            // Don't throw exception to allow application to continue
        }
    }
}
