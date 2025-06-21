-- 使用数据库
USE smart_diet;

-- 创建饮食日志表
CREATE TABLE IF NOT EXISTS meal_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    food_name VARCHAR(100) NOT NULL,
    calories DOUBLE NOT NULL,
    meal_time TIMESTAMP NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_meal_time (meal_time),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建运动日志表
CREATE TABLE IF NOT EXISTS exercise_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    activity VARCHAR(100) NOT NULL,
    duration INT NOT NULL,
    calories_burned DOUBLE NOT NULL,
    exercise_time TIMESTAMP NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_exercise_time (exercise_time),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 插入测试数据
INSERT INTO meal_log (user_id, food_name, calories, meal_time, notes, created_at)
VALUES 
(1, '测试早餐', 500, NOW(), '测试添加的早餐记录', NOW()),
(1, '测试午餐', 700, NOW(), '测试添加的午餐记录', NOW());

INSERT INTO exercise_log (user_id, activity, duration, calories_burned, exercise_time, notes, created_at)
VALUES 
(1, '测试跑步', 30, 300, NOW(), '测试添加的运动记录', NOW()),
(1, '测试健身', 45, 400, NOW(), '测试添加的健身记录', NOW()); 