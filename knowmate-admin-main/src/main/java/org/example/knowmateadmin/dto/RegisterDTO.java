package org.example.knowmateadmin.dto;

import lombok.Data;

/**
 * 注册数据传输对象
 */
@Data
public class RegisterDTO {
    private String username;
    private String email;
    private String password;
    private Integer age;
    private String gender;
    private Double height;
    private Double weight;
    // 可以根据需要添加其他字段，例如确认密码等
}