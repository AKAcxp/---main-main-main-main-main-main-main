package org.example.knowmateadmin.service;

import org.example.knowmateadmin.dto.ChatLogDTO;
import org.example.knowmateadmin.entity.ChatLog;

import java.util.List;

public interface ChatLogService {
    void saveChatLog(Long userId, ChatLogDTO chatLogDTO);

    List<ChatLogDTO> getChatHistory(Long userId);

    List<ChatLogDTO> getChatHistoryByUsername(String username);
}