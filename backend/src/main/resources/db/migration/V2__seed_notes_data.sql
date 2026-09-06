-- V2__seed_notes_data.sql
-- Seed data: Insert sample notes for development and testing

INSERT INTO notes (id, title, description, created_at, updated_at) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 
     'Welcome to Smart Notes', 
     'This is your first note! Smart Notes helps you organize your thoughts and ideas efficiently. You can create, edit, and delete notes as needed.',
     '2026-09-01 10:00:00', 
     '2026-09-01 10:00:00'),
    
    ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 
     'Meeting Notes - Q4 Planning', 
     'Discussed quarterly objectives and key results. Action items: 1) Review budget allocation, 2) Schedule team sync meetings, 3) Define project milestones.',
     '2026-09-02 14:30:00', 
     '2026-09-03 09:15:00'),
    
    ('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 
     'Recipe: Homemade Pizza', 
     'Ingredients: Pizza dough, tomato sauce, mozzarella cheese, basil, olive oil. Instructions: 1) Preheat oven to 475°F, 2) Roll out dough, 3) Add toppings, 4) Bake for 12-15 minutes.',
     '2026-09-03 18:45:00', 
     '2026-09-03 18:45:00'),
    
    ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 
     'Book Recommendations', 
     'Must-read books: "Atomic Habits" by James Clear, "Deep Work" by Cal Newport, "The Pragmatic Programmer" by Hunt & Thomas. Great for personal and professional development.',
     '2026-09-04 11:20:00', 
     '2026-09-05 16:30:00'),
    
    ('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', 
     'Project Ideas', 
     'Ideas for side projects: 1) Personal finance tracker, 2) Habit tracking app, 3) Recipe organizer with meal planning, 4) Workout log with progress charts.',
     '2026-09-05 09:00:00', 
     '2026-09-05 09:00:00'),
    
    ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', 
     'Travel Plans - Summer 2027', 
     'Destinations to consider: Barcelona (architecture and beaches), Tokyo (culture and food), Iceland (nature and northern lights). Budget: $3000-4000 per trip.',
     '2026-09-05 20:15:00', 
     '2026-09-06 08:00:00'),
    
    ('06eebc99-9c0b-4ef8-bb6d-6bb9bd380a77', 
     'Learning Goals', 
     'Skills to learn this year: Docker & Kubernetes, Advanced SQL optimization, System design patterns, GraphQL. Dedicate 5 hours per week to learning.',
     '2026-09-06 07:30:00', 
     '2026-09-06 07:30:00'),
    
    ('17eebc99-9c0b-4ef8-bb6d-6bb9bd380a88', 
     'Quick Note', 
     'Remember to backup all project files before the weekend. Cloud storage sync is scheduled for Friday evening.',
     '2026-09-06 08:15:00', 
     '2026-09-06 08:15:00');
