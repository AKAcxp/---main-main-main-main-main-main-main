package org.example.knowmateadmin.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseFixer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "fix-db".equals(args[0])) {
            fixDatabase();
        }
    }

    public void fixDatabase() {
        try {
            System.out.println("开始修复数据库表结构...");

            // 添加preference字段
            try {
                jdbcTemplate.execute("ALTER TABLE user_info ADD COLUMN preference TEXT COMMENT 'Taste preferences'");
                System.out.println("preference字段添加成功");
            } catch (Exception e) {
                System.out.println("preference字段可能已存在: " + e.getMessage());
            }

            // 添加avoid_food字段
            try {
                jdbcTemplate.execute("ALTER TABLE user_info ADD COLUMN avoid_food TEXT COMMENT 'Foods to avoid'");
                System.out.println("avoid_food字段添加成功");
            } catch (Exception e) {
                System.out.println("avoid_food字段可能已存在: " + e.getMessage());
            }

            // 查看表结构
            System.out.println("当前表结构:");
            jdbcTemplate.query("DESCRIBE user_info", (rs) -> {
                System.out.println(rs.getString("Field") + " - " + rs.getString("Type"));
            });

            System.out.println("数据库修复完成！");

        } catch (Exception e) {
            System.err.println("修复数据库时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
}