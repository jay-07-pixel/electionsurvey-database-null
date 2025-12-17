-- Election Survey Database Setup Script
-- Run this in phpMyAdmin SQL tab or MySQL command line

-- Create database (if not exists)
CREATE DATABASE IF NOT EXISTS election_survey;
USE election_survey;

-- Drop existing tables if they exist (to start fresh)
DROP TABLE IF EXISTS responses;
DROP TABLE IF EXISTS options;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS wards;
DROP TABLE IF EXISTS areas;
DROP TABLE IF EXISTS users;

-- 1. Users Table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Areas Table
CREATE TABLE areas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    area_name VARCHAR(100) NOT NULL
);

-- 3. Wards Table
CREATE TABLE wards (
    id INT PRIMARY KEY AUTO_INCREMENT,
    area_id INT NOT NULL,
    ward_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (area_id) REFERENCES areas(id) ON DELETE CASCADE
);

-- 4. Questions Table
CREATE TABLE questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    survey_id INT NOT NULL DEFAULT 1,
    question_text TEXT NOT NULL,
    type ENUM('text', 'single', 'multiple') NOT NULL
);

-- 5. Options Table
CREATE TABLE options (
    id INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT NOT NULL,
    option_text VARCHAR(255) NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- 6. Responses Table
CREATE TABLE responses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    survey_id INT NOT NULL,
    question_id INT NOT NULL,
    area_id INT NOT NULL,
    ward_id INT NOT NULL,
    selected_option_id INT NULL,
    answer_text TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (question_id) REFERENCES questions(id)
);

-- ============================================
-- SAMPLE DATA
-- ============================================

-- Insert Test User
INSERT INTO users (name, phone, password_hash) VALUES
('Test User', '1234567890', 'password123'),
('John Doe', '9876543210', 'password123'),
('Jane Smith', '5555555555', 'password123');

-- Insert Areas
INSERT INTO areas (area_name) VALUES
('Indira Nagar'),
('Deopur'),
('Rajendra Nagar'),
('Civil Lines'),
('MG Road');

-- Insert Wards
INSERT INTO wards (area_id, ward_name) VALUES
(1, 'Indira Nagar Ward 1'),
(1, 'Indira Nagar Ward 2'),
(1, 'Indira Nagar Ward 3'),
(2, 'Deopur Ward 1'),
(2, 'Deopur Ward 2'),
(3, 'Rajendra Nagar Ward 1'),
(3, 'Rajendra Nagar Ward 2'),
(4, 'Civil Lines Ward 1'),
(5, 'MG Road Ward 1');

-- Insert Survey Questions
INSERT INTO questions (survey_id, question_text, type) VALUES
(1, 'Write your email address', 'text'),
(1, 'Which party will win the election?', 'single'),
(1, 'What are the major issues in your ward?', 'multiple'),
(1, 'Rate the current government performance (1-10)', 'text'),
(1, 'Do you support the current policies?', 'single'),
(1, 'What improvements do you suggest?', 'text');

-- Insert Options for Single Choice Questions
INSERT INTO options (question_id, option_text) VALUES
(2, 'BJP'),
(2, 'NCP'),
(2, 'Congress'),
(2, 'AAP'),
(2, 'Other'),
(5, 'Yes, fully support'),
(5, 'Partially support'),
(5, 'Do not support'),
(5, 'Neutral');

-- Insert Options for Multiple Choice Questions
INSERT INTO options (question_id, option_text) VALUES
(3, 'Water Supply'),
(3, 'Roads and Infrastructure'),
(3, 'Electricity'),
(3, 'Sanitation'),
(3, 'Healthcare'),
(3, 'Education'),
(3, 'Security');

-- ============================================
-- VERIFY DATA
-- ============================================

-- Check all tables
SELECT 'Users' as TableName, COUNT(*) as Count FROM users
UNION ALL
SELECT 'Areas', COUNT(*) FROM areas
UNION ALL
SELECT 'Wards', COUNT(*) FROM wards
UNION ALL
SELECT 'Questions', COUNT(*) FROM questions
UNION ALL
SELECT 'Options', COUNT(*) FROM options;

