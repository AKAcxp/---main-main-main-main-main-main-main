-- ���preference��avoid_food�ֶΣ���������ڣ�
ALTER TABLE user_info 
ADD COLUMN IF NOT EXISTS preference TEXT COMMENT 'Taste preferences',
ADD COLUMN IF NOT EXISTS avoid_food TEXT COMMENT 'Foods to avoid'; 