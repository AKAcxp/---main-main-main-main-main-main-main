package org.example.knowmateadmin.controller;

import org.example.knowmateadmin.common.ApiResponse;
import org.example.knowmateadmin.dto.ChatLogDTO;
import org.example.knowmateadmin.mapper.UserMapper;
import org.example.knowmateadmin.entity.User;
import org.example.knowmateadmin.service.ChatLogService;
import org.example.knowmateadmin.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@RestController
@RequestMapping("/api/chatlog")
public class ChatLogController {

    @Autowired
    private ChatLogService chatLogService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole(\'USER\', \'NUTRITIONIST\', \'ADMIN\', \'TESTER\')")
    public ResponseEntity<ApiResponse<Void>> saveChatLog(@RequestBody ChatLogDTO chatLogDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Long userId = null;
        if (authentication.getPrincipal() instanceof User) {
            userId = ((User) authentication.getPrincipal()).getId();
        } else if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User springUser = (org.springframework.security.core.userdetails.User) authentication
                    .getPrincipal();
            User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", springUser.getUsername()));
            if (user != null) {
                userId = user.getId();
            }
        }

        if (userId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.<Void>error(400, "无法获取用户ID"));
        }

        chatLogService.saveChatLog(userId, chatLogDTO);
        return ResponseEntity.ok(ApiResponse.success("聊天记录保存成功", null));
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole(\'USER\', \'NUTRITIONIST\', \'ADMIN\', \'TESTER\')")
    public ResponseEntity<ApiResponse<List<ChatLogDTO>>> getChatHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Long userId = null;
        if (authentication.getPrincipal() instanceof User) {
            userId = ((User) authentication.getPrincipal()).getId();
        } else if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User springUser = (org.springframework.security.core.userdetails.User) authentication
                    .getPrincipal();
            User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", springUser.getUsername()));
            if (user != null) {
                userId = user.getId();
            }
        }

        if (userId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.<List<ChatLogDTO>>error(400, "无法获取用户ID"));
        }

        List<ChatLogDTO> chatHistory = chatLogService.getChatHistory(userId);
        return ResponseEntity.ok(ApiResponse.success("聊天历史获取成功", chatHistory));
    }
}