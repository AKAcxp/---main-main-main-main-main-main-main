CREATE TABLE IF NOT EXISTS user_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    height DOUBLE COMMENT 'Height in cm',
    weight DOUBLE COMMENT 'Weight in kg',
    goal VARCHAR(20) COMMENT 'Goal: gain muscle / lose fat / maintain',
    preference TEXT COMMENT 'Taste preferences',
    avoid_food TEXT COMMENT 'Foods to avoid',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username)
); 