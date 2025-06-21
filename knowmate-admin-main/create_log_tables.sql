-- ʹ�����ݿ�
USE smart_diet;

-- ������ʳ��־��
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

-- �����˶���־��
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

-- �����������
INSERT INTO meal_log (user_id, food_name, calories, meal_time, notes, created_at)
VALUES 
(1, '�������', 500, NOW(), '������ӵ���ͼ�¼', NOW()),
(1, '�������', 700, NOW(), '������ӵ���ͼ�¼', NOW());

INSERT INTO exercise_log (user_id, activity, duration, calories_burned, exercise_time, notes, created_at)
VALUES 
(1, '�����ܲ�', 30, 300, NOW(), '������ӵ��˶���¼', NOW()),
(1, '���Խ���', 45, 400, NOW(), '������ӵĽ����¼', NOW()); 