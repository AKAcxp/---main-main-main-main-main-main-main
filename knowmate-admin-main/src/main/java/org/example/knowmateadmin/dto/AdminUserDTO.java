package org.example.knowmateadmin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private Boolean isEnabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}