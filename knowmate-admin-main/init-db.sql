-- �������ݿ�
CREATE DATABASE IF NOT EXISTS smart_diet CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ʹ�����ݿ�
USE smart_diet;
 
-- ִ��Ǩ�ƽű�
SOURCE src/main/resources/db/migration/V1__create_user_info_table.sql;
SOURCE src/main/resources/db/migration/V2__create_user_info_table.sql;
SOURCE src/main/resources/db/migration/V3__create_food_table.sql;
SOURCE src/main/resources/db/migration/V4__insert_sample_foods.sql; 