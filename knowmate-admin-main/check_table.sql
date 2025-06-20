-- 检查user_info表结构
DESCRIBE user_info;

-- 查看表中的所有数据
SELECT * FROM user_info;

-- 如果preference和avoid_food字段不存在，添加它们
ALTER TABLE user_info ADD COLUMN IF NOT EXISTS preference TEXT;
ALTER TABLE user_info ADD COLUMN IF NOT EXISTS avoid_food TEXT;

-- 再次检查表结构
DESCRIBE user_info;

-- 更新测试数据
UPDATE user_info SET preference = '最终测试偏好口味', avoid_food = '最终测试饮食禁忌' WHERE username = 'cxp';

-- 查看更新后的数据
SELECT * FROM user_info WHERE username = 'cxp'; 