-- Insert test user for creating events
INSERT INTO users (email, password, name, college_name, role, created_at, updated_at) 
VALUES ('admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36CHqV36', 'Admin User', 'Tech University', 'CLUB_ADMIN', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Insert test events
INSERT INTO events (title, description, date, time, venue, category, collegename, organizer_name, organizer_email, image_url, created_by, created_at, updated_at)
VALUES 
  ('Tech Conference 2026', 'Annual technology conference with industry experts', '2026-05-14', '09:00 AM', 'Main Auditorium', 'TECH', 'Tech University', 'Admin User', 'admin@example.com', 'http://example.com/tech.jpg', 1, NOW(), NOW()),
  ('Cultural Festival', 'Annual cultural celebration featuring various performances', '2026-04-29', '02:00 PM', 'Outdoor Grounds', 'CULTURAL', 'Tech University', 'Admin User', 'admin@example.com', 'http://example.com/cultural.jpg', 1, NOW(), NOW()),
  ('Sports Tournament', 'Inter-college sports competition', '2026-05-24', '05:00 PM', 'Sports Complex', 'SPORTS', 'Tech University', 'Admin User', 'admin@example.com', 'http://example.com/sports.jpg', 1, NOW(), NOW()),
  ('Python Workshop', 'Learn Python programming basics', '2026-04-20', '10:00 AM', 'Lab Building Room 101', 'WORKSHOP', 'Tech University', 'Admin User', 'admin@example.com', 'http://example.com/workshop.jpg', 1, NOW(), NOW()),
  ('Data Science Seminar', 'Introduction to Data Science and Machine Learning', '2026-05-05', '03:00 PM', 'Convention Center', 'TECH', 'Tech University', 'Admin User', 'admin@example.com', 'http://example.com/datasci.jpg', 1, NOW(), NOW()),
  ('Music Concert', 'Live music performance by local artists', '2026-05-10', '07:00 PM', 'Open Theatre', 'CULTURAL', 'Tech University', 'Admin User', 'admin@example.com', 'http://example.com/concert.jpg', 1, NOW(), NOW())
ON CONFLICT DO NOTHING;
