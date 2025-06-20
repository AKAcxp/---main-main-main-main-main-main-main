package org.example.knowmateadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chat_log")
public class ChatLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String message;
    private Boolean isUser;
    private LocalDateTime createdAt;
}