package org.example.knowmateadmin.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户动态DTO
 */
@Data
public class PostDTO {

    private Long id;

    private Long userId;

    private String username; // 用户名，用于前端显示

    private String title;

    private String content;

    private List<String> mediaUrls; // 前端传递和接收的是数组形式

    private LocalDateTime createdAt;
}