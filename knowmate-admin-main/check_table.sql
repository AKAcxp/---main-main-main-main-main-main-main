-- ���user_info��ṹ
DESCRIBE user_info;

-- �鿴���е���������
SELECT * FROM user_info;

-- ���preference��avoid_food�ֶβ����ڣ��������
ALTER TABLE user_info ADD COLUMN IF NOT EXISTS preference TEXT;
ALTER TABLE user_info ADD COLUMN IF NOT EXISTS avoid_food TEXT;

-- �ٴμ���ṹ
DESCRIBE user_info;

-- ���²�������
UPDATE user_info SET preference = '���ղ���ƫ�ÿ�ζ', avoid_food = '���ղ�����ʳ����' WHERE username = 'cxp';

-- �鿴���º������
SELECT * FROM user_info WHERE username = 'cxp'; 