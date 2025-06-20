USE knowmate;

-- 添加preference字段
ALTER TABLE user_info ADD COLUMN IF NOT EXISTS preference TEXT COMMENT 'Taste preferences';

-- 添加avoid_food字段  
ALTER TABLE user_info ADD COLUMN IF NOT EXISTS avoid_food TEXT COMMENT 'Foods to avoid';

-- 查看表结构
DESCRIBE user_info;

-- 查看现有数据
SELECT * FROM user_info; 