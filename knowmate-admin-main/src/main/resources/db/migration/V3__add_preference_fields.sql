-- 添加preference和avoid_food字段（如果不存在）
ALTER TABLE user_info 
ADD COLUMN IF NOT EXISTS preference TEXT COMMENT 'Taste preferences',
ADD COLUMN IF NOT EXISTS avoid_food TEXT COMMENT 'Foods to avoid'; 