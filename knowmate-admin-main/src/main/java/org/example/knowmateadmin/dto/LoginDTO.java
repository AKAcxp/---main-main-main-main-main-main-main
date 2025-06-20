package org.example.knowmateadmin.dto;

import lombok.Data;

/**
 * 登录数据传输对象
 */
@Data
public class LoginDTO {
    private String username; // 可以是用户名或邮箱
    private String password;
} 