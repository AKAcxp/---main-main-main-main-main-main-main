package org.example.knowmateadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.knowmateadmin.dto.ChatLogDTO;
import org.example.knowmateadmin.entity.ChatLog;
import org.example.knowmateadmin.entity.User;
import org.example.knowmateadmin.mapper.ChatLogMapper;
import org.example.knowmateadmin.mapper.UserMapper;
import org.example.knowmateadmin.service.ChatLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatLogServiceImpl implements ChatLogService {

    @Autowired
    private ChatLogMapper chatLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void saveChatLog(Long userId, ChatLogDTO chatLogDTO) {
        ChatLog chatLog = new ChatLog();
        BeanUtils.copyProperties(chatLogDTO, chatLog);

        // 确保 message 字段不为 null，即使前端没有发送或为 null
        if (chatLog.getMessage() == null) {
            chatLog.setMessage("");
        }

        chatLog.setUserId(userId);
        chatLogMapper.insert(chatLog);
    }

    @Override
    public List<ChatLogDTO> getChatHistory(Long userId) {
        QueryWrapper<ChatLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("created_at").last("LIMIT 50");
        List<ChatLog> chatLogs = chatLogMapper.selectList(queryWrapper);
        return chatLogs.stream()
                .map(log -> {
                    ChatLogDTO dto = new ChatLogDTO();
                    BeanUtils.copyProperties(log, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatLogDTO> getChatHistoryByUsername(String username) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username);
        User user = userMapper.selectOne(userQueryWrapper);

        if (user != null) {
            return getChatHistory(user.getId());
        }
        return List.of(); // 返回空列表或者抛出异常，取决于业务需求
    }
}