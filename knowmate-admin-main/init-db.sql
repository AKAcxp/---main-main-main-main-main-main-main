-- 创建数据库
CREATE DATABASE IF NOT EXISTS smart_diet CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE smart_diet;
 
-- 执行迁移脚本
SOURCE src/main/resources/db/migration/V1__create_user_info_table.sql;
SOURCE src/main/resources/db/migration/V2__create_user_info_table.sql;
SOURCE src/main/resources/db/migration/V3__create_food_table.sql;
SOURCE src/main/resources/db/migration/V4__insert_sample_foods.sql; 