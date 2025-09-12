-- Insert test user (password: password123)
INSERT INTO users (username, password, full_name, email) VALUES (
    'testuser', 
    '$2a$10$4A7oKE7Jd4kK8vO3vC5BQOqON8w4dO1pN4/A4HyQ7fKL1sJ0Kw.Z2', 
    'Test User', 
    'test@example.com'
);

-- Insert test cards
INSERT INTO cards (card_number, card_holder_name, expiry_date, cvv, balance, status, card_type, owner_id) VALUES (
    '1234567890123456', 
    'Test User', 
    '2027-12-31', 
    '123', 
    1000.00, 
    'ACTIVE', 
    'DEBIT', 
    1
);

INSERT INTO cards (card_number, card_holder_name, expiry_date, cvv, balance, status, card_type, owner_id) VALUES (
    '9876543210987654', 
    'Test User', 
    '2028-06-30', 
    '456', 
    500.00, 
    'ACTIVE', 
    'CREDIT', 
    1
);