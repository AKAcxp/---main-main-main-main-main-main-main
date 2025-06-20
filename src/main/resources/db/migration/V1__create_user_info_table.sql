CREATE TABLE user_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    height DOUBLE COMMENT '��ߣ�cm��',
    weight DOUBLE COMMENT '���أ�kg��',
    goal VARCHAR(20) COMMENT 'Ŀ�꣺���� / ��֬ / ����',
    preference TEXT COMMENT '��ζƫ��',
    avoid_food TEXT COMMENT '�ɿ�',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES user(username)
); 