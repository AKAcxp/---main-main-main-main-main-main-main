CREATE TABLE user_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    height DOUBLE COMMENT '身高（cm）',
    weight DOUBLE COMMENT '体重（kg）',
    goal VARCHAR(20) COMMENT '目标：增肌 / 减脂 / 保持',
    preference TEXT COMMENT '口味偏好',
    avoid_food TEXT COMMENT '忌口',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES user(username)
); 