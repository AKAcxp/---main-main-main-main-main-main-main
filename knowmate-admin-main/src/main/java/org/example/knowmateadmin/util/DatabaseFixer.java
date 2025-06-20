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
            System.out.println("��ʼ�޸����ݿ��ṹ...");

            // ���preference�ֶ�
            try {
                jdbcTemplate.execute("ALTER TABLE user_info ADD COLUMN preference TEXT COMMENT 'Taste preferences'");
                System.out.println("preference�ֶ���ӳɹ�");
            } catch (Exception e) {
                System.out.println("preference�ֶο����Ѵ���: " + e.getMessage());
            }

            // ���avoid_food�ֶ�
            try {
                jdbcTemplate.execute("ALTER TABLE user_info ADD COLUMN avoid_food TEXT COMMENT 'Foods to avoid'");
                System.out.println("avoid_food�ֶ���ӳɹ�");
            } catch (Exception e) {
                System.out.println("avoid_food�ֶο����Ѵ���: " + e.getMessage());
            }

            // �鿴��ṹ
            System.out.println("��ǰ��ṹ:");
            jdbcTemplate.query("DESCRIBE user_info", (rs) -> {
                System.out.println(rs.getString("Field") + " - " + rs.getString("Type"));
            });

            System.out.println("���ݿ��޸���ɣ�");

        } catch (Exception e) {
            System.err.println("�޸����ݿ�ʱ����: " + e.getMessage());
            e.printStackTrace();
        }
    }
}