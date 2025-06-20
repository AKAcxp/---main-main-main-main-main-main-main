package org.example.knowmateadmin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatLogDTO {
    private String message;
    private Boolean isUser;
    private LocalDateTime createdAt;
}