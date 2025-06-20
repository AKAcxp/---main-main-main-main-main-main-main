package org.example.knowmateadmin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "org.example.knowmateadmin.*" })
// 临时改回扫描两个包，以解决 UserInfoMapper 找不到的问题
// TODO: 长远来看，应将 UserInfoMapper 和相关类迁移到 org.example.knowmateadmin 包下
@MapperScan(basePackages = { "org.example.knowmateadmin.mapper" })
public class KnowmateAdminApplication {

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        SpringApplication.run(KnowmateAdminApplication.class, args);
    }

}
