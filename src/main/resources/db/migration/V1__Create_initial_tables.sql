--liquibase formatted sql

--changeset author:bank-app version:1.0

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bank_cards (
    id BIGSERIAL PRIMARY KEY,
    card_number VARCHAR(255) UNIQUE NOT NULL,
    masked_number VARCHAR(20) NOT NULL,
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    cardholder_name VARCHAR(100) NOT NULL,
    expiry_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    from_card_id BIGINT NOT NULL REFERENCES bank_cards(id) ON DELETE CASCADE,
    to_card_id BIGINT NOT NULL REFERENCES bank_cards(id) ON DELETE CASCADE,
    amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_bank_cards_owner_id ON bank_cards(owner_id);
CREATE INDEX idx_bank_cards_status ON bank_cards(status);
CREATE INDEX idx_bank_cards_expiry_date ON bank_cards(expiry_date);
CREATE INDEX idx_transactions_from_card_id ON transactions(from_card_id);
CREATE INDEX idx_transactions_to_card_id ON transactions(to_card_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);

INSERT INTO users (username, password, email, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'admin@bank.com', 'ADMIN'),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'user1@bank.com', 'USER'),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'user2@bank.com', 'USER');

INSERT INTO bank_cards (card_number, masked_number, owner_id, cardholder_name, expiry_date, status, balance) VALUES
('1234567890123456', '**** **** **** 3456', 2, 'Иван Иванов', '2028-12-31', 'ACTIVE', 10000.00),
('9876543210987654', '**** **** **** 7654', 2, 'Иван Иванов', '2028-12-31', 'ACTIVE', 5000.00),
('1111222233334444', '**** **** **** 4444', 3, 'Петр Петров', '2028-12-31', 'ACTIVE', 7500.00);
