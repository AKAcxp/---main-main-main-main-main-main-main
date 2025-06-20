package org.example.knowmateadmin.controller;

import org.example.knowmateadmin.dto.UserInfoDTO;
import org.example.knowmateadmin.entity.UserInfo;
import org.example.knowmateadmin.service.UserInfoService;
import org.example.knowmateadmin.utils.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/info")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        String username = jwtUtil.getUsernameFromRequest(request);
        UserInfo info = userInfoService.getUserInfo(username);
        return ResponseEntity.ok(info);
    }

    @PostMapping
    public ResponseEntity<?> updateUserInfo(@RequestBody UserInfoDTO dto, HttpServletRequest request) {
        String username = jwtUtil.getUsernameFromRequest(request);
        userInfoService.saveOrUpdate(username, dto);
        return ResponseEntity.ok("±£´æ³É¹¦");
    }
}