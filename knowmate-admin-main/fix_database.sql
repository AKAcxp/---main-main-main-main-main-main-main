USE knowmate;

-- ���preference�ֶ�
ALTER TABLE user_info ADD COLUMN IF NOT EXISTS preference TEXT COMMENT 'Taste preferences';

-- ���avoid_food�ֶ�  
ALTER TABLE user_info ADD COLUMN IF NOT EXISTS avoid_food TEXT COMMENT 'Foods to avoid';

-- �鿴��ṹ
DESCRIBE user_info;

-- �鿴��������
SELECT * FROM user_info; 